package ist.meic.cmu.asyncTasks.termite;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import ist.meic.cmu.utils.WifiMessage;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;

public class SendCommTask extends AsyncTask<String, String, WifiMessage> {

    private String TAG = this.getClass().getSimpleName();
    private SimWifiP2pSocket mCliSocket;
    private Callback mCallback;
    private WifiMessage message;

    public SendCommTask(SimWifiP2pSocket mCliSocket, WifiMessage message, Callback mCallback) {
        this.mCliSocket = mCliSocket;
        this.mCallback = mCallback;
        this.message = message;
    }

    public interface Callback {
        void onSuccess(WifiMessage responseMessage);
    }

    @Override
    protected WifiMessage doInBackground(String... msg) {
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(mCliSocket.getOutputStream());
            outputStream.writeObject(message);
            ObjectInputStream inputStream = new ObjectInputStream(mCliSocket.getInputStream());
            WifiMessage message = (WifiMessage) inputStream.readObject();
            outputStream.close();
            inputStream.close();
            mCliSocket.close();
            return message;
        } catch (IOException e) {
            Log.d(TAG, "doInBackground: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(WifiMessage result) {
        mCallback.onSuccess(result);

    }
}
