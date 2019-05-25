package ist.meic.cmu.asyncTasks.dropbox;

import android.os.AsyncTask;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.users.FullAccount;

import ist.meic.cmu.utils.LoggerFactory;

/**
 * Async task for getting user account info
 */
public class GetCurrentAccountTask extends AsyncTask<Void, Void, FullAccount> {

    private final DbxClientV2 mDbxClient;
    private final Callback mCallback;
    private Exception mException;

    public interface Callback {
        void onComplete(FullAccount result);
        void onError(Exception e);
    }

    public GetCurrentAccountTask(DbxClientV2 dbxClient, Callback callback) {
        mDbxClient = dbxClient;
        mCallback = callback;
    }

    @Override
    protected void onPostExecute(FullAccount account) {
        super.onPostExecute(account);
        LoggerFactory.log(this.getClass().getSimpleName()+": Finished");
        if (mException != null) {
            mCallback.onError(mException);
        } else {
            mCallback.onComplete(account);
        }
    }

    @Override
    protected FullAccount doInBackground(Void... params) {
        LoggerFactory.log(this.getClass().getSimpleName()+": Initialized");
        try {
            return mDbxClient.users().getCurrentAccount();

        } catch (DbxException e) {
            mException = e;
        }
        return null;
    }

}