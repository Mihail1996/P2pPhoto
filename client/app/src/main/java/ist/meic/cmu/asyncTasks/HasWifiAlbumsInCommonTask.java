package ist.meic.cmu.asyncTasks;

import android.content.SharedPreferences;
import android.os.AsyncTask;

import java.io.IOException;

import ist.meic.cmu.utils.LoggerFactory;
import ist.meic.cmu.utils.RetrofitFactory;
import ist.meic.cmu.utils.ServerService;
import retrofit2.Call;
import retrofit2.Response;

public class HasWifiAlbumsInCommonTask extends AsyncTask<Void, String, Boolean> {

    private final Callback mCallback;
    private SharedPreferences sharedPreferences;
    private String username;
    private String albumName;


    public interface Callback {
        void onSuccess(Boolean result);

        void onError();
    }

    public HasWifiAlbumsInCommonTask(SharedPreferences sharedPreferences, String username, String albumName, Callback callback) {
        mCallback = callback;
        this.sharedPreferences = sharedPreferences;
        this.username = username;
        this.albumName = albumName;
    }


    @Override
    protected Boolean doInBackground(Void... voids) {
        LoggerFactory.log(this.getClass().getSimpleName() + ": Initialized");
        ServerService service = RetrofitFactory.getServerService();
        Call<Boolean> call = service.hasWifiAlbumsInCommon(sharedPreferences.getString("cookie", null), username, albumName);
        try {
            Response<Boolean> response = call.execute();
            return response.body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Boolean response) {
        super.onPostExecute(response);
        LoggerFactory.log(this.getClass().getSimpleName() + ": Finished");
        if (response != null) {
            mCallback.onSuccess(response);
        } else {
            mCallback.onError();
        }
    }
}