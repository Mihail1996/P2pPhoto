package ist.meic.cmu.asyncTasks;

import android.content.SharedPreferences;
import android.os.AsyncTask;

import java.io.IOException;

import ist.meic.cmu.utils.LoggerFactory;
import ist.meic.cmu.utils.RetrofitFactory;
import ist.meic.cmu.utils.ServerService;
import retrofit2.Response;

public class AddUserToWifiAlbumTask extends AsyncTask<Void, String, String> {
    private final String TAG = this.getClass().getSimpleName();
    private SharedPreferences sharedPreferences;
    private Callback mCallback;
    private String albumName;
    private String username;

    public interface Callback {
        void onFinish(String message);

    }

    public AddUserToWifiAlbumTask(String username, String albumName, SharedPreferences sharedPreferences, Callback callback) {
        this.sharedPreferences = sharedPreferences;
        mCallback = callback;
        this.albumName = albumName;
        this.username = username;
    }

    @Override
    protected String doInBackground(Void... args) {
        LoggerFactory.log(this.getClass().getSimpleName() + ": Initialized");

        ServerService service = RetrofitFactory.getServerService();

        try {
            Response<String> response = service.addUserToWifiAlbum(sharedPreferences.getString("cookie", null), username, albumName).execute();
            return response.body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Error occurred";
    }


    @Override
    protected void onPostExecute(String message) {
        LoggerFactory.log(this.getClass().getSimpleName() + ": Finished");
        mCallback.onFinish(message);
    }
}
