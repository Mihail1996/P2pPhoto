package ist.meic.cmu.asyncTasks;


import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import ist.meic.cmu.utils.WifiLocalAlbum;


public class ListWifiPhotosTask extends AsyncTask<Void, Void, List<String>> {
    private String TAG = this.getClass().getSimpleName();
    private String fileUri;
    private String albumName;
    private Callback mCallback;
    private static File defaultPath = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOWNLOADS);
    private static File path = new File(defaultPath, "/p2pPhotoAlbums/");

    public interface Callback {
        void onSuccess(List<String> list);
    }


    public ListWifiPhotosTask(String albumName, Callback callback) {
        this.fileUri = fileUri;
        this.albumName = albumName;
        mCallback = callback;
    }


    @Override
    protected List<String> doInBackground(Void... voids) {
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
                List<String> returnPhotos = new ArrayList<>(wifiAlbum.getCatalog().keySet());
                Log.d(TAG, "doInBackground: " + returnPhotos);
                return returnPhotos;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            return new ArrayList<>();
        }
        return new ArrayList<>();
    }

    @Override
    protected void onPostExecute(List<String> result) {
        mCallback.onSuccess(result);
    }


}
