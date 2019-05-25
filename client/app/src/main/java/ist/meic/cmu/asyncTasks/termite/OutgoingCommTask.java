package ist.meic.cmu.asyncTasks.termite;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.UnknownHostException;

import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;

public class OutgoingCommTask extends AsyncTask<String, Void, SimWifiP2pSocket> {
    private String TAG = this.getClass().getSimpleName();
    private int port;
    private Exception mException;
    private Callback mCallback;
    private String ip;

    public interface Callback {
        void onPreExecute();

        void onSuccess(SimWifiP2pSocket cliSocket);

        void onError(Exception e);
    }


    public OutgoingCommTask(int port, String ip, Callback callback) {
        this.port = port;
        this.ip = ip;
        mCallback = callback;
    }

    @Override
    protected void onPreExecute() {
        mCallback.onPreExecute();

    }

    @Override
    protected SimWifiP2pSocket doInBackground(String... params) {
        try {
            return new SimWifiP2pSocket(ip, port);
        } catch (UnknownHostException e) {
            mException = e;
            Log.d(TAG, "doInBackground: Unknown Host " + e.getMessage());
        } catch (IOException e) {
            mException = e;
            Log.d(TAG, "doInBackground: IO error:" + e.getMessage());

        }
        return null;
    }

    @Override
    protected void onPostExecute(SimWifiP2pSocket result) {
        if (result != null) {
            mCallback.onSuccess(result);
        } else {
            mCallback.onError(mException);
        }
    }
}