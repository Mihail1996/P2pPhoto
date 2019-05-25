package ist.meic.cmu.asyncTasks;

import android.content.SharedPreferences;
import android.os.AsyncTask;

import java.io.IOException;

import ist.meic.cmu.utils.LoggerFactory;
import ist.meic.cmu.utils.RetrofitFactory;
import ist.meic.cmu.utils.ServerService;
import retrofit2.Call;
import retrofit2.Response;

public class GetUserTask extends AsyncTask<Void, String, String> {

    private static final String TAG = GetUserTask.class.getName();
    private final Callback mCallback;
    private SharedPreferences sharedPreferences;
    private String username;


    public interface Callback {
        void onSuccess(String response);

        void onError();
    }

    public GetUserTask(SharedPreferences sharedPreferences, String username, Callback callback) {
        mCallback = callback;
        this.sharedPreferences = sharedPreferences;
        this.username = username;
    }


    @Override
    protected String doInBackground(Void... voids) {
        LoggerFactory.log(this.getClass().getSimpleName() + ": Initialized");
        ServerService service = RetrofitFactory.getServerService();
        Call<String> call = service.getUser(sharedPreferences.getString("cookie", null), username);
        try {
            Response<String> response = call.execute();
            return response.body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String response) {
        super.onPostExecute(response);
        LoggerFactory.log(this.getClass().getSimpleName() + ": Finished");
        if (response != null) {
            mCallback.onSuccess(response);
        } else {
            mCallback.onError();
        }
    }
}