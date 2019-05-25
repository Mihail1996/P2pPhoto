package ist.meic.cmu.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.File;


public class PicassoClient {
    private static Picasso sPicasso;
    private static final String TAG = PicassoClient.class.getSimpleName();
    private static File path = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOWNLOADS);
    private static File file = new File(path, "/p2pPhotoCache");


    public static void init(Context context) {
        PicassoClient.init(context, 50);
    }

    public static void init(Context context, long max) {
        if (!path.exists()) {
            if (!path.mkdirs()) {
                Log.d(TAG, "init: Folder can't be created");
            }
        } else if (!path.isDirectory()) {
            Log.d(TAG, "init: Path not a directory");
        }

        Picasso.Builder builder = new Picasso.Builder(context);
        builder.indicatorsEnabled(true);
        sPicasso = builder.downloader(new OkHttp3Downloader(file, max)).build();
        LoggerFactory.log(TAG + ": Picasso initialized with " + max + "MB of size");
    }

    public static Picasso getPicasso() {
        return sPicasso;
    }
}
