package ist.meic.cmu.asyncTasks;

import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ist.meic.cmu.utils.LoggerFactory;
import ist.meic.cmu.utils.RetrofitFactory;
import ist.meic.cmu.utils.ServerService;
import retrofit2.Call;
import retrofit2.Response;


public class ListPhotosFromAlbumCatalogs extends AsyncTask<Void, String, List<String>> {
    private String TAG = this.getClass().getSimpleName();
    private final Callback mCallback;
    private List<String> mCatalog;
    private int catalogsSize;

    @Override
    protected List<String> doInBackground(Void... voids) {
        LoggerFactory.log(this.getClass().getSimpleName() + ": Initialized");
        catalogsSize = mCatalog.size();
        List<String> syncPhotos = Collections.synchronizedList(new ArrayList<>());
        List<Boolean> finishedCalls = new ArrayList<>();
        Log.d(TAG, "inicio: " + finishedCalls);
        ServerService service = RetrofitFactory.getDbService();
        for (String catalog : mCatalog) {
            catalog = catalog.replace("https://www.dropbox.com/", "");
            catalog = catalog.replace("?dl=1", "");
            catalog = catalog.replace("?dl=0", "");
            Call<String> call = service.getPhotos(catalog);
            call.enqueue(new retrofit2.Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    synchronized (finishedCalls) {
                        syncPhotos.addAll(Arrays.asList(response.body().split(" ")));
                        finishedCalls.add(true);
                        if (finishedCalls.size() == catalogsSize) {
                            finishedCalls.notify();
                        }
                    }
                }
                @Override
                public void onFailure(Call<String> call, Throwable t) {
                }
            });
        }

        synchronized (finishedCalls) {
            try {
                finishedCalls.wait();
                return syncPhotos;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public interface Callback {
        void onPhotosRetrieved(List<String> result);

        void onError();

    }

    public ListPhotosFromAlbumCatalogs(List<String> catalogs, ListPhotosFromAlbumCatalogs.Callback callback) {
        mCatalog = catalogs;
        mCallback = callback;
    }


    @Override
    protected void onPostExecute(List<String> result) {
        super.onPostExecute(result);
        LoggerFactory.log(this.getClass().getSimpleName() + ": Finished");
        if (result == null) {
            mCallback.onError();
        } else {
            mCallback.onPhotosRetrieved(result);
        }
    }


}
