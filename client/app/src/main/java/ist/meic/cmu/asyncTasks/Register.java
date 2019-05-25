package ist.meic.cmu.asyncTasks;

import android.os.AsyncTask;

import java.io.IOException;

import ist.meic.cmu.utils.LoggerFactory;
import ist.meic.cmu.utils.RetrofitFactory;
import ist.meic.cmu.utils.ServerService;
import retrofit2.Response;


public class Register extends AsyncTask<String, String, Integer> {
    private static final String TAG = Register.class.getName();
    private final Callback mCallback;


    public interface Callback {
        void onUserRegistered();

        void onError();
    }

    public Register(Callback callback) {
        mCallback = callback;
    }

    @Override
    protected Integer doInBackground(String... args) {
        LoggerFactory.log(this.getClass().getSimpleName()+": Initialized");
        return register(args[0], args[1], args[2]);
    }

    private Integer register(String username, String password, String token) {
        ServerService service = RetrofitFactory.getServerService();

        try {
            Response<Void> response = service.register(username, password, token).execute();
            return response.code();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;

    }

    @Override
    protected void onPostExecute(Integer response) {
        LoggerFactory.log(this.getClass().getSimpleName()+": Finished");
        if (response == 201) {
            mCallback.onUserRegistered();

        } else {
            mCallback.onError();
        }
    }
}