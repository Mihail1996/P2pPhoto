package ist.meic.cmu.asyncTasks;


import android.content.SharedPreferences;
import android.os.AsyncTask;

import java.io.IOException;

import ist.meic.cmu.utils.LoggerFactory;
import ist.meic.cmu.utils.RetrofitFactory;
import ist.meic.cmu.utils.ServerService;
import retrofit2.Response;


public class CreateAlbumTask extends AsyncTask<String, Void, Integer> {
    private static final String TAG = CreateAlbumTask.class.getName();
    private final Callback mCallback;
    private SharedPreferences sharedPreferences;


    public interface Callback {
        void onAlbumCreated();

        void onError();
    }

    public CreateAlbumTask(Callback callback, SharedPreferences sharedPreferences) {
        mCallback = callback;
        this.sharedPreferences = sharedPreferences;
    }


    @Override
    protected Integer doInBackground(String... strings) {
        LoggerFactory.log(this.getClass().getSimpleName()+": Initialized");
        ServerService service = RetrofitFactory.getServerService();
        try {
            Response<Void> response = service.createAlbum(strings[0], sharedPreferences.getString("cookie", null)).execute();
            return response.code();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    protected void onPostExecute(Integer status) {
        LoggerFactory.log(this.getClass().getSimpleName()+": Finished");
        if (status == 201) {
            mCallback.onAlbumCreated();
        } else {
            mCallback.onError();
        }
    }
}