package ist.meic.cmu.asyncTasks;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import ist.meic.cmu.utils.LoggerFactory;
import ist.meic.cmu.utils.RetrofitFactory;
import ist.meic.cmu.utils.ServerService;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class GetLogTask extends AsyncTask<Void, String, Void> {

    private static final String TAG = GetLogTask.class.getName();
    private final Callback mCallback;
    private SharedPreferences sharedPreferences;


    public interface Callback {
        void onSuccess(Void log);

        void onError();
    }

    public GetLogTask(SharedPreferences sharedPreferences, Callback callback) {
        mCallback = callback;
        this.sharedPreferences = sharedPreferences;
    }


    @Override
    protected Void doInBackground(Void... voids) {
        LoggerFactory.log(this.getClass().getSimpleName() + ": Initialized");
        ServerService service = RetrofitFactory.getServerService();
        Call<ResponseBody> call = service.getLog(sharedPreferences.getString("cookie", null));
        try {
            File path = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS);
            File file = new File(path, "p2pPhotosServerLog.txt");
            Response<ResponseBody> response = call.execute();
            OutputStream outputStream = new FileOutputStream(file);
            outputStream.write(response.body().bytes());
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void response) {
        super.onPostExecute(response);
        mCallback.onSuccess(response);
    }
}