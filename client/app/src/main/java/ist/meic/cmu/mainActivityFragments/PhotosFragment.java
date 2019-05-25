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
import android.os.Bundle;
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

import com.dropbox.core.v2.sharing.SharedLinkMetadata;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.List;

import ist.meic.cmu.BuildConfig;
import ist.meic.cmu.R;
import ist.meic.cmu.adapters.PhotosAdapter;
import ist.meic.cmu.asyncTasks.AddUserToAlbumTask;
import ist.meic.cmu.asyncTasks.ListAlbumCatalogsTask;
import ist.meic.cmu.asyncTasks.ListPhotosFromAlbumCatalogs;
import ist.meic.cmu.asyncTasks.UpdateCatalogTask;
import ist.meic.cmu.asyncTasks.dropbox.DownloadPhotoTask;
import ist.meic.cmu.asyncTasks.dropbox.UploadFileTask;
import ist.meic.cmu.utils.DropboxClientFactory;
import ist.meic.cmu.utils.LoggerFactory;
import ist.meic.cmu.utils.PicassoClient;

public class PhotosFragment extends Fragment {
    private String albumName;
    private static final String TAG = PhotosFragment.class.getName();
    private PhotosAdapter mPhotosAdapter;
    private String mSelectedPhoto;
    private static final int PICKFILE_REQUEST_CODE = 1;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_photos, container, false);
    }

    public PhotosFragment(String albumName) {
        this.albumName = albumName;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LoggerFactory.log(this.getClass().getSimpleName() + ": Initialized");
        FloatingActionButton uploadFab = getView().findViewById(R.id.photos_fab_button);
        uploadFab.setOnClickListener(v -> launchFilePicker());

        final SharedPreferences sharedPref = getContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String token = sharedPref.getString("token", null);

        FloatingActionButton addUserFab = getView().findViewById(R.id.photos_addUser_button);
        addUserFab.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("User Name");
            final EditText input = new EditText(getContext());
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);
            builder.setPositiveButton("OK", (dialog, which) -> {
                String user = input.getText().toString();
                new AddUserToAlbumTask(user, albumName, sharedPref, message ->
                        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show()).execute();
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            builder.show();
        });


        if (DropboxClientFactory.getClient() == null) {
            DropboxClientFactory.init(token);
        }

        RecyclerView recyclerView = getView().findViewById(R.id.photos_list);
        mPhotosAdapter = new PhotosAdapter(PicassoClient.getPicasso(), this::downloadPhoto);


        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setAdapter(mPhotosAdapter);
        loadData();
        mSelectedPhoto = null;
    }


    private void loadData() {
        final ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.setMessage("Loading");
        dialog.show();

        new ListAlbumCatalogsTask(result -> {
            dialog.dismiss();
            if (result.isEmpty()) {
                Toast.makeText(getContext(), "Empty album, add photos maybe", Toast.LENGTH_LONG).show();
            } else {
                Log.d(TAG, "onCatalogLoaded: " + result);
                new ListPhotosFromAlbumCatalogs(result, new ListPhotosFromAlbumCatalogs.Callback() {
                    @Override
                    public void onPhotosRetrieved(List<String> result) {
                        mPhotosAdapter.setPhotos(result);
                    }

                    @Override
                    public void onError() {
                        Toast.makeText(getContext(), "Something went wrong while retrieving the photos", Toast.LENGTH_LONG).show();
                    }
                }).execute();
            }
        }, getContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)).execute(albumName);
    }


    private void downloadPhoto(final String url) {

        final ProgressDialog spinner = new ProgressDialog(getContext());
        spinner.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        spinner.setCancelable(false);
        spinner.setMessage("Downloading");
        spinner.show();
        new DownloadPhotoTask(DropboxClientFactory.getClient(), new DownloadPhotoTask.Callback() {
            @Override
            public void onDownloadComplete(File result) {
                spinner.dismiss();
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(Uri.fromFile(result));
                getContext().sendBroadcast(intent);
                Toast.makeText(getContext(), "File Downloaded", Toast.LENGTH_LONG).show();
                viewFileInExternalApp(result);
            }

            @Override
            public void onError(Exception e) {
                spinner.dismiss();
                Toast.makeText(getContext(), "Error Downloading", Toast.LENGTH_LONG).show();

            }
        }).execute(url);

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

        new UploadFileTask(getContext(), DropboxClientFactory.getClient(), new UploadFileTask.Callback() {
            @Override
            public void onUploadComplete(SharedLinkMetadata result) {
                new UpdateCatalogTask(albumName, result, getContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE), DropboxClientFactory.getClient(), new UpdateCatalogTask.Callback() {
                    @Override
                    public void onSuccess(Boolean result) {
                        dialog.dismiss();
                        Toast.makeText(getContext(), "File Uploaded", Toast.LENGTH_SHORT)
                                .show();
                        loadData();
                    }

                    @Override
                    public void onError() {
                        dialog.dismiss();

                        Log.e(TAG, "Failed to upload file. UpdateCatalog Failed");
                        Toast.makeText(getContext(),
                                "An error has occurred",
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                }).execute();

            }

            @Override
            public void onError(Exception e) {
                dialog.dismiss();
                Toast.makeText(getContext(),
                        "An error has occurred",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }).execute(fileUri, "/p2pPhoto");
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
}
