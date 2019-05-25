package ist.meic.cmu.asyncTasks;

import android.content.SharedPreferences;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ist.meic.cmu.utils.LoggerFactory;
import ist.meic.cmu.utils.RetrofitFactory;
import ist.meic.cmu.utils.ServerService;
import retrofit2.Call;
import retrofit2.Response;


public class ListAlbumsTask extends AsyncTask<String, Void, List<String>> {
    private static final String TAG = ListAlbumsTask.class.getName();
    private final Callback mCallback;
    private SharedPreferences sharedPreferences;


    public interface Callback {
        void onAlbumsLoaded(List<String> result);
    }

    public ListAlbumsTask(Callback callback, SharedPreferences sharedPreferences) {
        mCallback = callback;
        this.sharedPreferences = sharedPreferences;
    }


    @Override
    protected List<String> doInBackground(String... strings) {
        LoggerFactory.log(this.getClass().getSimpleName()+": Initialized");
        ServerService service = RetrofitFactory.getServerService();
        Call<ArrayList<String>> call = service.getUserAlbums(sharedPreferences.getString("cookie", null));
        try {
            Response<ArrayList<String>> response = call.execute();
            return response.body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<String> list) {
        super.onPostExecute(list);
        LoggerFactory.log(this.getClass().getSimpleName()+": Finished");
        mCallback.onAlbumsLoaded(list);
    }
}
