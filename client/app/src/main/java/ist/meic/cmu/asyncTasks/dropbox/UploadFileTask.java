package ist.meic.cmu.asyncTasks.dropbox;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.WriteMode;
import com.dropbox.core.v2.sharing.SharedLinkMetadata;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import ist.meic.cmu.utils.LoggerFactory;
import ist.meic.cmu.utils.UriHelpers;


/**
 * Async task to upload a file to a directory
 */
public class UploadFileTask extends AsyncTask<String, Void, SharedLinkMetadata> {
    private final String TAG = this.getClass().getSimpleName();
    private final Context mContext;
    private final DbxClientV2 mDbxClient;
    private final Callback mCallback;
    private Exception mException;

    public interface Callback {
        void onUploadComplete(SharedLinkMetadata result);

        void onError(Exception e);
    }

    public UploadFileTask(Context context, DbxClientV2 dbxClient, Callback callback) {
        mContext = context;
        mDbxClient = dbxClient;
        mCallback = callback;
    }

    @Override
    protected void onPostExecute(SharedLinkMetadata result) {
        super.onPostExecute(result);
        LoggerFactory.log(this.getClass().getSimpleName()+": Finished");
        if (mException != null) {
            mCallback.onError(mException);
        } else if (result == null) {
            mCallback.onError(null);
        } else {
            mCallback.onUploadComplete(result);
        }
    }

    @Override
    protected SharedLinkMetadata doInBackground(String... params) {
        LoggerFactory.log(this.getClass().getSimpleName() + ": Initialized");
        String localUri = params[0];
        File localFile = UriHelpers.getFileForUri(mContext, Uri.parse(localUri));
        FileMetadata fileMetadata;

        if (localFile != null) {
            String remoteFolderPath = params[1];

            String remoteFileName = UUID.randomUUID().toString() + localFile.getName();

            try {
                mDbxClient.files().createFolderV2("/p2pPhoto");
            } catch (DbxException e) {
                e.printStackTrace();
            }
            try (InputStream inputStream = new FileInputStream(localFile)) {
                fileMetadata = mDbxClient.files().uploadBuilder(remoteFolderPath + "/" + remoteFileName)
                        .withMode(WriteMode.ADD)
                        .uploadAndFinish(inputStream);
                Log.d(TAG, "doInBackground:  " + fileMetadata);
                return mDbxClient.sharing().createSharedLinkWithSettings(fileMetadata.getId());
            } catch (DbxException | IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
