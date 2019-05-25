package ist.meic.cmu.utils;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LoggerFactory {
    private static final String TAG = "LoggerFactory";
    private static Logger logger;

    public static void init() {
        if (logger != null) {
            return;
        }
        logger = Logger.getLogger("p2pPhotoLog");
        File path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS);
        File file = new File(path, "p2pPhotosLog.txt");
        try {
            System.setProperty("java.util.logging.SimpleFormatter.format",
                    "[%1$tF %1$tl:%1$tM:%1$tS.%1$tL] [%4$-7s] %5$s %n");

            FileHandler fh = new FileHandler(file.getAbsolutePath());
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
            logger.info("Logger Initialized");
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Logger getLogger() {
        return logger;
    }

    public static void log(String message) {
        Log.d(TAG, "log: message " + message);
        logger.info(message);
    }
}
