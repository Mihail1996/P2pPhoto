package ist.meic.cmu.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.Messenger;
import android.util.Log;

import ist.meic.cmu.R;
import ist.meic.cmu.asyncTasks.termite.IncommingCommTask;
import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;
import pt.inesc.termite.wifidirect.service.SimWifiP2pService;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketManager;

public class WifiService {

    private static Context mContext;
    private static String TAG = WifiMessage.class.getSimpleName();

    private static SimWifiP2pManager mManager = null;
    private static SimWifiP2pManager.Channel mChannel = null;
    private static Messenger mService = null;
    private static boolean mBound = false;
    private static SharedPreferences sharedPreferences;


    public static SimWifiP2pManager getManager() {
        return mManager;
    }

    public static boolean isBound() {
        return mBound;
    }

    public static SimWifiP2pManager.Channel getChannel() {
        return mChannel;
    }

    private static SimWifiP2pBroadcastReceiver mReceiver;

    public static void init(Context context) {
        mContext = context;
        SimWifiP2pSocketManager.Init(mContext);
        sharedPreferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        // register broadcast receiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_STATE_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_PEERS_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_NETWORK_MEMBERSHIP_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_GROUP_OWNERSHIP_CHANGED_ACTION);
        mReceiver = new SimWifiP2pBroadcastReceiver();
        mContext.registerReceiver(mReceiver, filter);
        Intent intent = new Intent(mContext, SimWifiP2pService.class);
        mContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        mBound = true;

        new IncommingCommTask(Integer.parseInt(mContext.getString(R.string.port)), sharedPreferences, message ->
                Log.d(TAG, "onSuccess: message received " + message.getID() + " " + message.getSenderUsername())).executeOnExecutor(
                AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private static ServiceConnection mConnection = new ServiceConnection() {
        // callbacks for service binding, passed to bindService()

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);
            mManager = new SimWifiP2pManager(mService);
            mChannel = mManager.initialize(mContext, mContext.getMainLooper(), null);
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mService = null;
            mManager = null;
            mChannel = null;
            mBound = false;
        }
    };

    public static void pause() {
//        mContext.unregisterReceiver(mReceiver);
    }

    public static void wifiOff() {
        if (mBound) {
            mContext.unbindService(mConnection);
            mBound = false;
        }
    }
}
