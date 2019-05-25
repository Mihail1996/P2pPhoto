package ist.meic.cmu.mainActivityFragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ist.meic.cmu.BuildConfig;
import ist.meic.cmu.R;
import ist.meic.cmu.adapters.PhotosAdapter;
import ist.meic.cmu.asyncTasks.AddUserToWifiAlbumTask;
import ist.meic.cmu.asyncTasks.AddWifiPhotoToAlbum;
import ist.meic.cmu.asyncTasks.ListWifiPhotosTask;
import ist.meic.cmu.asyncTasks.termite.OutgoingCommTask;
import ist.meic.cmu.asyncTasks.termite.SendCommTask;
import ist.meic.cmu.utils.LoggerFactory;
import ist.meic.cmu.utils.PicassoClient;
import ist.meic.cmu.utils.WifiLocalAlbum;
import ist.meic.cmu.utils.WifiMessage;
import ist.meic.cmu.utils.WifiService;
import pt.inesc.termite.wifidirect.SimWifiP2pDevice;
import pt.inesc.termite.wifidirect.SimWifiP2pDeviceList;
import pt.inesc.termite.wifidirect.SimWifiP2pInfo;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;

public class WifiPhotosFragment extends Fragment implements SimWifiP2pManager.PeerListListener, SimWifiP2pManager.GroupInfoListener {
    private String albumName;
    private static final String TAG = WifiPhotosFragment.class.getName();
    private PhotosAdapter mPhotosAdapter;
    private static final int PICKFILE_REQUEST_CODE = 1;
    private SharedPreferences sharedPref;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_photos, container, false);
    }

    public WifiPhotosFragment(String albumName) {
        this.albumName = albumName;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LoggerFactory.log(this.getClass().getSimpleName() + ": Initialized");
        FloatingActionButton uploadFab = getView().findViewById(R.id.photos_fab_button);
        uploadFab.setOnClickListener(v -> launchFilePicker());

        sharedPref = getContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        FloatingActionButton addUserFab = getView().findViewById(R.id.photos_addUser_button);
        addUserFab.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("User Name");
            final EditText input = new EditText(getContext());
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);
            builder.setPositiveButton("OK", (dialog, which) -> {
                String user = input.getText().toString();
                new AddUserToWifiAlbumTask(user, albumName, sharedPref, message ->
                        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show()).execute();
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            builder.show();
        });

        RecyclerView recyclerView = getView().findViewById(R.id.photos_list);
        mPhotosAdapter = new PhotosAdapter(PicassoClient.getPicasso(), url -> {
            Log.d(TAG, "onPhotoClicked: Clicked " + url.replace("file:", ""));
            url = url.replace("file:", "");
            File file = new File(url);
            viewFileInExternalApp(file);
        });


        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setAdapter(mPhotosAdapter);
        loadData();
    }


    private void loadData() {
        final ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.setMessage("Loading");
        dialog.show();
        if (WifiService.isBound()) {
            WifiService.getManager().requestGroupInfo(WifiService.getChannel(), WifiPhotosFragment.this);
        } else {
            Toast.makeText(getContext(), "Service not bound", Toast.LENGTH_SHORT).show();
        }
        dialog.dismiss();

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICKFILE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {

                uploadFile(data.getData().toString());
            }
        }
    }

    private void uploadFile(String fileUri) {
        final ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.setMessage("Uploading");
        dialog.show();
        new AddWifiPhotoToAlbum(fileUri, getContext(), albumName, list -> {
            mPhotosAdapter.setPhotos(list);
            dialog.dismiss();
        }).execute();


    }

    private void launchFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, PICKFILE_REQUEST_CODE);
    }

    private void viewFileInExternalApp(File result) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String ext = result.getName().substring(result.getName().indexOf(".") + 1);
        String type = mime.getMimeTypeFromExtension(ext);
        Uri photoURI = FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID + ".provider", result);
        intent.setDataAndType(photoURI, type);
        PackageManager manager = getContext().getPackageManager();
        List<ResolveInfo> resolveInfo = manager.queryIntentActivities(intent, 0);
        if (resolveInfo.size() > 0) {
            startActivity(intent);
        }
    }

    @Override
    public void onGroupInfoAvailable(SimWifiP2pDeviceList simWifiP2pDeviceList, SimWifiP2pInfo simWifiP2pInfo) {
        List<String> photosList1 = new ArrayList<>();
        for (String deviceName : simWifiP2pInfo.getDevicesInNetwork()) {
            SimWifiP2pDevice device = simWifiP2pDeviceList.getByName(deviceName);
            new OutgoingCommTask(Integer.parseInt(getString(R.string.port)), device.getVirtIp(), new OutgoingCommTask.Callback() {
                @Override
                public void onPreExecute() {
                    Log.d(TAG, "onPreExecute: Connecting to " + deviceName + " " + device.getVirtIp());
                }

                @Override
                public void onSuccess(SimWifiP2pSocket cliSocket) {
                    WifiMessage message = new WifiMessage(sharedPref.getString("username", null), albumName);
                    File defaultPath = Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DOWNLOADS);
                    File path = new File(defaultPath, "/p2pPhotoAlbums/");
                    File file = new File(path, albumName);
                    if (file.exists()) {
                        ObjectInputStream inputStream;
                        try {
                            inputStream = new ObjectInputStream(new FileInputStream(file));
                            WifiLocalAlbum wifiAlbum = (WifiLocalAlbum) inputStream.readObject();
                            inputStream.close();
                            message.setHashList(wifiAlbum.getHashList());
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    new SendCommTask(cliSocket, message, responseMessage -> {

                        if (responseMessage != null && responseMessage.HasAlbumInCommon()) {
                            if (!responseMessage.getPhotos().isEmpty()) {
                                Log.d(TAG, "onSuccess: NUMBER OF PHOTOS: " + responseMessage.getPhotos().size());
                                File cachePath = new File(defaultPath, "/p2pPhotoWifiPhotosReceived/");
                                if (!cachePath.exists()) {
                                    if (!cachePath.mkdirs()) {
                                        Log.d(TAG, "init: Folder can't be created");
                                    }
                                } else if (!cachePath.isDirectory()) {
                                    Log.d(TAG, "init: Path not a directory");
                                }

                                for (Map.Entry<byte[], String> entry : responseMessage.getPhotos().entrySet()) {
                                    OutputStream outputStream;
                                    try {
                                        File photoFile = new File(cachePath, entry.getValue());
                                        new AddWifiPhotoToAlbum(photoFile.toURI().toString(), getContext(), albumName, list -> {

                                        }).execute();
                                        photosList1.add(photoFile.toURI().toString());
                                        outputStream = new FileOutputStream(photoFile);
                                        outputStream.write(entry.getKey());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                if (mPhotosAdapter.getPhotos() != null) {
                                    photosList1.addAll(mPhotosAdapter.getPhotos());
                                    mPhotosAdapter.setPhotos(photosList1);
                                } else {
                                    mPhotosAdapter.setPhotos(photosList1);
                                }
                            }
                        }
                    }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }

                @Override
                public void onError(Exception e) {

                }
            }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        new ListWifiPhotosTask(albumName, list -> {
            if (mPhotosAdapter.getPhotos() != null) {
                list.addAll(mPhotosAdapter.getPhotos());
                mPhotosAdapter.setPhotos(list);
            } else {
                mPhotosAdapter.setPhotos(list);
            }

        }).execute();

    }

    @Override
    public void onPeersAvailable(SimWifiP2pDeviceList simWifiP2pDeviceList) {

    }
}
