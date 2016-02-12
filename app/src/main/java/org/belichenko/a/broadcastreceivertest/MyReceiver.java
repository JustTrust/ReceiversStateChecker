package org.belichenko.a.broadcastreceivertest;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;

import butterknife.OnClick;

/**
 *
 */
public class MyReceiver extends BroadcastReceiver implements MyConstants {

    SharedPreferences mPrefs;

    @Override
    public void onReceive(Context context, Intent intent) {
        mPrefs = context.getSharedPreferences(MAIN_PREFERENCE, Context.MODE_PRIVATE);
        Log.d(LOG_TAG, "onReceive() called with: " + " intent = [" + intent.getAction() + "]");
        switch (intent.getAction()) {

            case "android.intent.action.AIRPLANE_MODE":
                airPlaneModeSelector(context);
                break;

            case "android.net.wifi.WIFI_STATE_CHANGED":
                int wfState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
                wifiStateSelector(context, wfState);
                break;

            case "android.bluetooth.adapter.action.STATE_CHANGED":
                int blState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                blueToothStateSelector(context, blState);
                break;
            default:
                Log.d(LOG_TAG, "onReceive: " + "we get another action from intent.getAction()");
                break;
        }
    }

    private void blueToothStateSelector(Context context, int blState) {
        switch (blState) {
            case BluetoothAdapter.STATE_ON:
                sendNotification(context,
                        context.getResources().getText(R.string.bl_on).toString(),
                        context.getResources().getText(R.string.more).toString(),
                        R.drawable.ic_action_bluetooth,
                        Color.BLUE);
                sendCallToActivity(context);
                break;
            case BluetoothAdapter.STATE_OFF:
                sendNotification(context,
                        context.getResources().getText(R.string.bl_off).toString(),
                        context.getResources().getText(R.string.more).toString(),
                        R.drawable.ic_action_bluetooth,
                        Color.WHITE);
                sendCallToActivity(context);
                break;
            case BluetoothAdapter.ERROR:
                Log.d(LOG_TAG, "onReceive: " + "BluetoothAdapter.ERROR");
                break;
        }
    }

    private void wifiStateSelector(Context context, int wfState) {
        switch (wfState) {
            case WifiManager.WIFI_STATE_DISABLED:
                sendNotification(context,
                        context.getResources().getText(R.string.wf_off).toString(),
                        context.getResources().getText(R.string.more).toString(),
                        R.drawable.ic_action_network_wifi,
                        Color.YELLOW);
                sendCallToActivity(context);
                break;
            case WifiManager.WIFI_STATE_ENABLED:
                sendNotification(context,
                        context.getResources().getText(R.string.wf_on).toString(),
                        context.getResources().getText(R.string.more).toString(),
                        R.drawable.ic_action_network_wifi,
                        Color.MAGENTA);
                sendCallToActivity(context);
                break;
            case WifiManager.WIFI_STATE_UNKNOWN:
                Log.d(LOG_TAG, "onReceive: " + "WifiManager.WIFI_STATE_UNKNOWN");
                break;
        }
    }

    private void airPlaneModeSelector(Context context) {
        boolean isAirplaneModeOn;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
             /* API 17 and above */
            isAirplaneModeOn = Settings.Global.
                    getInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
        } else {
            /* below */
            isAirplaneModeOn = Settings.System.
                    getInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) != 0;
        }
        if (isAirplaneModeOn) {
            sendNotification(context,
                    context.getResources().getText(R.string.air_on).toString(),
                    context.getResources().getText(R.string.more).toString(),
                    R.drawable.ic_action_airplane_mode_on,
                    Color.GREEN);
            sendCallToActivity(context);
        } else {
            sendNotification(context,
                    context.getResources().getText(R.string.air_off).toString(),
                    context.getResources().getText(R.string.more).toString(),
                    R.drawable.ic_action_airplane_mode_off,
                    Color.RED);
            sendCallToActivity(context);
        }
    }

    private void sendNotification(Context cont, String title, String text, int icon, int light) {

        Intent notificationIntent = new Intent(cont, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(cont,
                ID, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationManager nm = (NotificationManager) cont.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification.Builder builder = new Notification.Builder(cont);
        builder.setContentIntent(contentIntent)
                .setSmallIcon(icon)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentText(text)
                .setTicker(title)
                .setLights(light, 1200, 2000)
                .setVibrate(new long[]{500, 500, 300, 300});

        Notification n = builder.build();
        nm.notify(TAG, NOTIFY_ID, n);
    }

    private void sendCallToActivity(Context context) {
        Intent i = new Intent();
        i.setAction("org.belichenko.a.broadcastreceivertest.INTENT");
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
        localBroadcastManager.sendBroadcast(i);

        Log.d(TAG, "sendCallToActivity() called with: " + "custom Intent");
    }
}