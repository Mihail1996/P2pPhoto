package ist.meic.cmu.asyncTasks;

import android.content.SharedPreferences;
import android.os.AsyncTask;

import java.io.IOException;

import ist.meic.cmu.utils.LoggerFactory;
import ist.meic.cmu.utils.RetrofitFactory;
import ist.meic.cmu.utils.ServerService;
import retrofit2.Response;

public class Login extends AsyncTask<String, String, Integer> {
    private final String TAG = this.getClass().getSimpleName();
    private SharedPreferences sharedPreferences;
    private Callback mCallback;
    private String cookie;

    public interface Callback {
        void onFinish();

        void onError();
    }

    public Login(SharedPreferences sharedPreferences, Callback callback) {
        this.sharedPreferences = sharedPreferences;
        mCallback = callback;
    }

    @Override
    protected Integer doInBackground(String... args) {
        return login(args[0], args[1]);
    }

    private Integer login(String username, String password) {
        LoggerFactory.log(this.getClass().getSimpleName()+": Initialized");
        ServerService service = RetrofitFactory.getServerService();

        try {
            Response<Void> response = service.login(username, password).execute();
            if (response.headers().get("SET-COOKIE")!=null){
                cookie = response.headers().get("SET-COOKIE").split(";")[0];
            }
            return response.code();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;


    }

    @Override
    protected void onPostExecute(Integer status) {
        LoggerFactory.log(this.getClass().getSimpleName()+": Finished");
        if (status == 200) {

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("cookie", cookie);
            editor.apply();
            mCallback.onFinish();
        } else {
            mCallback.onError();
        }
    }
}


