package ist.meic.cmu.asyncTasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ist.meic.cmu.utils.Photo;
import ist.meic.cmu.utils.UriHelpers;
import ist.meic.cmu.utils.WifiLocalAlbum;


public class AddWifiPhotoToAlbum extends AsyncTask<Void, Void, List<String>> {
    private String TAG = this.getClass().getSimpleName();
    private String fileUri;
    private String albumName;
    private Callback mCallback;
    private Context mContext;
    private static File defaultPath = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOWNLOADS);
    private static File path = new File(defaultPath, "/p2pPhotoAlbums/");

    public interface Callback {
        void onSuccess(List<String> list);
    }


    public AddWifiPhotoToAlbum(String fileUri, Context context, String albumName, Callback callback) {
        this.fileUri = fileUri;
        this.albumName = albumName;
        mCallback = callback;
        mContext = context;
    }


    @Override
    protected List<String> doInBackground(Void... voids) {
        File localFile = UriHelpers.getFileForUri(mContext, Uri.parse(fileUri));
        if (!path.exists()) {
            if (!path.mkdirs()) {
                Log.d(TAG, "init: Folder can't be created");
            }
        } else if (!path.isDirectory()) {
            Log.d(TAG, "init: Path not a directory");
        }
        File file = new File(path, albumName);
        if (file.exists()) {
            try {
                ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file));
                WifiLocalAlbum wifiAlbum = (WifiLocalAlbum) inputStream.readObject();
                inputStream.close();
                FileInputStream fileInputStream = new FileInputStream(localFile);
                Bitmap myBitmap = BitmapFactory.decodeStream(fileInputStream);
                fileInputStream.close();
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                boolean success = myBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
                if (success) {
                    MessageDigest md = MessageDigest.getInstance("SHA-1");
                    byte[] hashBytes = md.digest(byteStream.toByteArray());
                    String hash = Base64.encodeToString(hashBytes, Base64.DEFAULT);
                    Log.d(TAG, "doInBackground: HASH" + hash);
                    wifiAlbum.getHashList().add(hash);
                    wifiAlbum.getCatalog().put(localFile.toURI().toString(), new Photo(localFile.getName(), hash));
                }
                List<String> returnPhotos = new ArrayList<>(wifiAlbum.getCatalog().keySet());
                try {
                    ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(file));
                    os.writeObject(wifiAlbum);
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return returnPhotos;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        } else {
            WifiLocalAlbum wifiAlbum = new WifiLocalAlbum(albumName);
            Map<String, Photo> photos = new HashMap<>();

            try {
                FileInputStream fileInputStream = new FileInputStream(localFile);
                Bitmap myBitmap = BitmapFactory.decodeStream(fileInputStream);
                fileInputStream.close();
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                boolean success = myBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
                if (success) {
                    MessageDigest md = MessageDigest.getInstance("SHA-1");
                    byte[] hashBytes = md.digest(byteStream.toByteArray());
                    String hash = Base64.encodeToString(hashBytes, Base64.DEFAULT);
                    Log.d(TAG, "doInBackground: HASH" + hash);
                    wifiAlbum.getHashList().add(hash);
                    wifiAlbum.setCatalog(photos);
                    photos.put(localFile.toURI().toString(), new Photo(localFile.getName(), hash));
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

            try {
                ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(file));
                os.writeObject(wifiAlbum);
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            List<String> returnPhotos = new ArrayList<>();
            returnPhotos.add(localFile.toURI().toString());
            return returnPhotos;
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<String> result) {
        mCallback.onSuccess(result);
    }


}
