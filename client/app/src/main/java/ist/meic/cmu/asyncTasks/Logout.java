package ist.meic.cmu.asyncTasks;

import android.content.SharedPreferences;
import android.os.AsyncTask;

import java.io.IOException;

import ist.meic.cmu.utils.LoggerFactory;
import ist.meic.cmu.utils.RetrofitFactory;
import ist.meic.cmu.utils.ServerService;
import ist.meic.cmu.utils.DropboxClientFactory;
import retrofit2.Response;

public class Logout extends AsyncTask<Void, Void, Integer> {
    private final String TAG = this.getClass().getSimpleName();
    private SharedPreferences sharedPreferences;


    public interface Callback {
        void onError();
    }

    public Logout(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;

    }

    @Override
    protected Integer doInBackground(Void... voids) {
        return logout();
    }

    private Integer logout() {
        LoggerFactory.log(this.getClass().getSimpleName()+": Initialized");
        ServerService service = RetrofitFactory.getServerService();

        try {
            Response<Void> response = service.logout(sharedPreferences.getString("cookie", null)).execute();
            return response.code();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;

    }

    protected void onPostExecute(Integer integer) {
        LoggerFactory.log(this.getClass().getSimpleName()+": Finished");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        DropboxClientFactory.resetClient();


    }
}