package ist.meic.cmu.asyncTasks.termite;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

import ist.meic.cmu.asyncTasks.HasWifiAlbumsInCommonTask;
import ist.meic.cmu.utils.Photo;
import ist.meic.cmu.utils.WifiLocalAlbum;
import ist.meic.cmu.utils.WifiMessage;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketServer;

public class IncommingCommTask extends AsyncTask<Void, String, Void> {
    private String TAG = this.getClass().getSimpleName();
    private Callback mCallback;
    private SimWifiP2pSocketServer mSrvSocket;
    private int port;
    private SharedPreferences sharedPreferences;
    private static File defaultPath = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOWNLOADS);
    private static File path = new File(defaultPath, "/p2pPhotoAlbums/");

    public IncommingCommTask(int port, SharedPreferences sharedPreferences, Callback callback) {
        mCallback = callback;
        this.port = port;
        this.sharedPreferences = sharedPreferences;
    }

    public interface Callback {
        void onSuccess(WifiMessage message);
    }

    @Override
    protected Void doInBackground(Void... params) {

        Log.d(TAG, "IncommingCommTask started (" + this.hashCode() + ").");
        try {
            mSrvSocket = new SimWifiP2pSocketServer(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (!Thread.currentThread().isInterrupted()) {
            try {
                SimWifiP2pSocket sock = mSrvSocket.accept();
                try {
                    ObjectOutputStream outputStream = new ObjectOutputStream(sock.getOutputStream());
                    ObjectInputStream objectInputStream = new ObjectInputStream(sock.getInputStream());

                    WifiMessage message = (WifiMessage) objectInputStream.readObject();

                    WifiMessage responseMessage = new WifiMessage(sharedPreferences.getString("username", null), message.getAlbumName());
                    new HasWifiAlbumsInCommonTask(sharedPreferences, message.getSenderUsername(), message.getAlbumName(), new HasWifiAlbumsInCommonTask.Callback() {
                        @Override
                        public void onSuccess(Boolean result) {
                            Log.d(TAG, "onSuccess: " + result);
                            if (result) {
                                responseMessage.setHasAlbumInCommon(true);
                                File file = new File(path, message.getAlbumName());
                                if (file.exists()) {
                                    try {
                                        ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file));
                                        WifiLocalAlbum wifiAlbum = (WifiLocalAlbum) inputStream.readObject();
                                        inputStream.close();
                                        if (wifiAlbum.getCatalog().isEmpty()) {
                                            synchronized (responseMessage) {
                                                responseMessage.notify();
                                            }
                                        } else {
                                            for (Map.Entry<String, Photo> photo : wifiAlbum.getCatalog().entrySet()) {
                                                if (message.getHashList() != null && !message.getHashList().contains(photo.getValue().getHash())) {
                                                    File photoFile = new File(photo.getKey().replace("file:", ""));
                                                    InputStream photoInputStream = new FileInputStream(photoFile);
                                                    Bitmap myBitmap = BitmapFactory.decodeStream(photoInputStream);
                                                    ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                                                    boolean success = myBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
                                                    if (success) {
                                                        responseMessage.getPhotos().put(byteStream.toByteArray(), photoFile.getName());
                                                    }
                                                }
                                            }
                                            synchronized (responseMessage) {
                                                responseMessage.notify();
                                            }
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    } catch (ClassNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                synchronized (responseMessage) {
                                    responseMessage.notify();
                                }
                            }

                        }

                        @Override
                        public void onError() {

                        }
                    }).execute();
                    synchronized (responseMessage) {
                        try {
                            responseMessage.wait();
                            outputStream.writeObject(responseMessage);
                            outputStream.close();
                            objectInputStream.close();
                            sock.close();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    Log.d("Error reading socket:", e.getMessage());
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                Log.d("Error socket:", e.getMessage());
                break;
            }
        }
        return null;
    }
}