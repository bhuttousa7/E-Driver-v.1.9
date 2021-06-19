package com.telogix.telogixcaptain.Utils;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.net.InetAddress;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Oscar on 20/03/15.
 */
public class InternetService extends Service {

    // Constant
    public static String TAG_INTERVAL = "interval";
    public static String TAG_URL_PING = "url_ping";
    public static String TAG_ACTIVITY_NAME = "activity_name";

    private int interval;
    private String url_ping;
    private String activity_name;

    private Timer mTimer = null;

    ConnectionServiceCallback mConnectionServiceCallback;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public interface ConnectionServiceCallback {
        void hasInternetConnection();
        void hasNoInternetConnection();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        try {


            interval = intent.getIntExtra(TAG_INTERVAL, 10);
            url_ping = intent.getStringExtra(TAG_URL_PING);
            activity_name = intent.getStringExtra(TAG_ACTIVITY_NAME);

            try {
                mConnectionServiceCallback = (ConnectionServiceCallback) Class.forName(activity_name).newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            mTimer = new Timer();
            mTimer.scheduleAtFixedRate(new CheckForConnection(), 0, interval * 1000);

            return super.onStartCommand(intent, flags, startId);

        }catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    class CheckForConnection extends TimerTask{
        @Override
        public void run() {
            Intent i = new Intent(Constants.MY_INTERNET_ACTION_KEY);
            if(isNetworkAvailable())
            {
                i.putExtra("internet",true);
                 sendBroadcast(i);
//                mConnectionServiceCallback.hasInternetConnection();

            }else
            {
                i.putExtra("internet",false);
             //   mConnectionServiceCallback.hasNoInternetConnection();
                sendBroadcast(i);
            }

        }
    }

    @Override
    public void onDestroy() {
        mTimer.cancel();
        super.onDestroy();
    }

    private boolean isNetworkAvailable(){
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            //You can replace it with your name
            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }


    }

}
