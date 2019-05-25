package ist.meic.cmu.asyncTasks;

import android.content.SharedPreferences;
import android.os.AsyncTask;

import java.io.IOException;

import ist.meic.cmu.utils.LoggerFactory;
import ist.meic.cmu.utils.RetrofitFactory;
import ist.meic.cmu.utils.ServerService;
import retrofit2.Call;
import retrofit2.Response;

public class GetTokenTask extends AsyncTask<Void, String, String> {

    private static final String TAG = GetTokenTask.class.getName();
    private final Callback mCallback;
    private SharedPreferences sharedPreferences;


    public interface Callback {
        void onSuccess(String token);

        void onError();
    }

    public GetTokenTask(Callback callback, SharedPreferences sharedPreferences) {
        mCallback = callback;
        this.sharedPreferences = sharedPreferences;
    }


    @Override
    protected String doInBackground(Void... voids) {
        LoggerFactory.log(this.getClass().getSimpleName()+": Initialized");
        ServerService service = RetrofitFactory.getServerService();
        Call<String> call = service.getDbToken(sharedPreferences.getString("cookie", null));
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
        LoggerFactory.log(this.getClass().getSimpleName()+": Finished");
        if (response != null) {
            mCallback.onSuccess(response);
        } else {
            mCallback.onError();
        }
    }
}