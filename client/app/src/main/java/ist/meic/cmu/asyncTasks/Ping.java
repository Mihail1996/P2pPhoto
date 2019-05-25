package ist.meic.cmu.asyncTasks;

import android.os.AsyncTask;

import java.io.IOException;

import ist.meic.cmu.utils.LoggerFactory;
import ist.meic.cmu.utils.RetrofitFactory;
import ist.meic.cmu.utils.ServerService;

public class Ping extends AsyncTask<Void, String, Integer> {
    private final String TAG = this.getClass().getSimpleName();
    private Callback mCallback;

    public interface Callback {
        void onFinish();

        void onError();
    }

    public Ping(Callback callback) {
        mCallback = callback;
    }

    @Override
    protected Integer doInBackground(Void... voids) {
        LoggerFactory.log(this.getClass().getSimpleName()+": Initialized");
        ServerService service = RetrofitFactory.getServerService();

        try {
            return service.ping().execute().code();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;

    }

    @Override
    protected void onPostExecute(Integer status) {
        LoggerFactory.log(this.getClass().getSimpleName()+": Finished");
        if (status == 200) {
            mCallback.onFinish();
        } else
            mCallback.onError();
    }
}