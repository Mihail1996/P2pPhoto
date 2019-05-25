package ist.meic.cmu.asyncTasks.dropbox;


import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import ist.meic.cmu.utils.LoggerFactory;


public class DownloadPhotoTask extends AsyncTask<String, Long, File> {
    private String TAG = this.getClass().getSimpleName();
    private final DbxClientV2 mDbxClient;
    private final DownloadPhotoTask.Callback mCallback;
    private Exception mException;

    public interface Callback {
        void onDownloadComplete(File result);

        void onError(Exception e);
    }

    public DownloadPhotoTask(DbxClientV2 dbxClient, DownloadPhotoTask.Callback callback) {
        mDbxClient = dbxClient;
        mCallback = callback;
    }


    @Override
    protected void onPostExecute(File result) {
        super.onPostExecute(result);
        LoggerFactory.log(this.getClass().getSimpleName()+": Finished");
        if (mException != null) {
            mCallback.onError(mException);
        } else {
            mCallback.onDownloadComplete(result);
        }
    }

    @Override
    protected File doInBackground(String... params) {
        LoggerFactory.log(this.getClass().getSimpleName()+": Initialized");
        try {
            Log.d(TAG, "bind: " + mDbxClient.sharing().getSharedLinkMetadata(params[0]));
            File path = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS);
            String filename = params[0].substring(params[0].lastIndexOf('/') + 1);
            filename = filename.replace("?dl=0", "");
            filename = filename.replace("?dl=1", "");
            File file = new File(path, filename);

            if (!path.exists()) {
                if (!path.mkdirs()) {
                    mException = new RuntimeException("Unable to create directory: " + path);
                }
            } else if (!path.isDirectory()) {
                mException = new IllegalStateException("Download path is not a directory: " + path);
                return null;
            }

            try (OutputStream outputStream = new FileOutputStream(file)) {
                mDbxClient.sharing().getSharedLinkFile(params[0]).download(outputStream);
            }

            return file;
        } catch (DbxException | IOException e) {
            mException = e;
        }

        return null;
    }

}