package ist.meic.cmu.asyncTasks;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.WriteMode;
import com.dropbox.core.v2.sharing.SharedLinkMetadata;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ist.meic.cmu.utils.LoggerFactory;
import ist.meic.cmu.utils.RetrofitFactory;
import ist.meic.cmu.utils.ServerService;
import retrofit2.Call;
import retrofit2.Response;


public class UpdateCatalogTask extends AsyncTask<Void, Void, Boolean> {

    private final String TAG = this.getClass().getSimpleName();
    private final Callback mCallback;
    private SharedLinkMetadata sharedLinkMetadata;
    private String albumName;
    private SharedPreferences sharedPreferences;
    private String catalogUrl;
    private DbxClientV2 dbxClientV2;

    public UpdateCatalogTask(String albumName, SharedLinkMetadata sharedLinkMetadata, SharedPreferences sharedPreferences, DbxClientV2 dbxClientV2, Callback mCallback) {
        this.mCallback = mCallback;
        this.sharedLinkMetadata = sharedLinkMetadata;
        this.albumName = albumName;
        this.sharedPreferences = sharedPreferences;
        this.dbxClientV2 = dbxClientV2;
    }

    public interface Callback {
        void onSuccess(Boolean result);

        void onError();
    }


    @Override
    protected Boolean doInBackground(Void... voids) {
        LoggerFactory.log(this.getClass().getSimpleName()+": Initialized");
        ServerService service = RetrofitFactory.getServerService();
        Call<String> call = service.getMyAlbumCatalog(sharedPreferences.getString("cookie", null), albumName);
        try {
            Response<String> response = call.execute();
            catalogUrl = response.body();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (catalogUrl == null || catalogUrl.equals("null") || catalogUrl.equals("")) {
            File path = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS);
            File file = new File(path, albumName + sharedPreferences.getString("username", null) + ".txt");
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
                writer.append(sharedLinkMetadata.getUrl().replace("?dl=0", "?dl=1"));
                writer.close();
                try (InputStream inputStream = new FileInputStream(file)) {
                    FileMetadata fileMetadata = dbxClientV2.files().uploadBuilder("/p2pPhoto/" + albumName + sharedPreferences.getString("username", null) + ".txt")
                            .withMode(WriteMode.OVERWRITE)
                            .uploadAndFinish(inputStream);
                    SharedLinkMetadata sharedLink = dbxClientV2.sharing().createSharedLinkWithSettings(fileMetadata.getId());
                    service.addCatalogToAlbum(sharedPreferences.getString("cookie", null), sharedLink.getUrl(), albumName).execute();

                } catch (DbxException | IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            try {
                SharedLinkMetadata sharedLinkMeta = dbxClientV2.sharing().getSharedLinkMetadata(catalogUrl);
                File path = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS);
                File file = new File(path, sharedLinkMeta.getName());

                try (OutputStream outputStream = new FileOutputStream(file)) {
                    dbxClientV2.sharing().getSharedLinkFile(sharedLinkMeta.getUrl()).download(outputStream);
                } catch (DbxException | IOException e) {
                    e.printStackTrace();
                }

                BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
                writer.append(" ").append(sharedLinkMetadata.getUrl().replace("?dl=0", "?dl=1"));
                writer.close();

                try (InputStream inputStream = new FileInputStream(file)) {
                    dbxClientV2.files().uploadBuilder(sharedLinkMeta.getId())
                            .withMode(WriteMode.OVERWRITE)
                            .uploadAndFinish(inputStream);
                } catch (DbxException | IOException e) {
                    e.printStackTrace();
                }
            } catch (DbxException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        LoggerFactory.log(this.getClass().getSimpleName()+": Finished");
        mCallback.onSuccess(result);
    }
}
