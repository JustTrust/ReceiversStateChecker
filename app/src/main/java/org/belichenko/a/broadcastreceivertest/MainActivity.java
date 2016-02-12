package org.belichenko.a.broadcastreceivertest;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity implements MyConstants {

    @Bind(R.id.air_switch)
    Switch air_switch;
    @Bind(R.id.wifi_switch)
    Switch wifi_switch;
    @Bind(R.id.bt_switch)
    Switch bt_switch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Send WIFI STATE CHANGED", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                sendMessage();
            }
        });
        checkReceiversState();
    }

    private void sendMessage() {
        Intent i = new Intent();
        i.setAction("android.net.wifi.WIFI_STATE_CHANGED");
        sendBroadcast(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void checkReceiversState() {

        boolean isAirplaneModeOn;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
             /* API 17 and above */
            isAirplaneModeOn = Settings.Global.
                    getInt(getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
        } else {
            /* below */
            isAirplaneModeOn = Settings.System.
                    getInt(getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) != 0;
        }
        if (isAirplaneModeOn) {
            air_switch.setChecked(true);
        } else {
            air_switch.setChecked(false);
        }

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Log.d(TAG, "checkReceiversState() - Device does not support Bluetooth");
        } else {
            if (mBluetoothAdapter.isEnabled()) {
                bt_switch.setChecked(true);
            } else {
                bt_switch.setChecked(false);
            }
        }

        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if (wifi.isWifiEnabled()) {
            wifi_switch.setChecked(true);
        } else {
            wifi_switch.setChecked(false);
        }
    }

    @OnClick(R.id.wifi_switch)
    public void clickOnWifi(View view) {
        WifiManager wManager = (WifiManager)this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wManager.setWifiEnabled(wifi_switch.isChecked());
    }

    @OnClick(R.id.air_switch)
    public void clickOnAirplane(View view) {

//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
//            Settings.System.putInt(
//                    getContentResolver(),
//                    Settings.System.AIRPLANE_MODE_ON, air_switch.isChecked()? 0 : 1);
//        } else {
//            Settings.Global.putInt(
//                    getContentResolver(),
//                    Settings.Global.AIRPLANE_MODE_ON, air_switch.isChecked() ? 0 : 1);
//        }
    }

    @OnClick(R.id.bt_switch)
    public void clickOnBT(){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bt_switch.isChecked()) {
            mBluetoothAdapter.enable();
        }else {
            mBluetoothAdapter.disable();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myBroadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkReceiversState();
        IntentFilter iff = new IntentFilter("org.belichenko.a.broadcastreceivertest.INTENT");
        LocalBroadcastManager.getInstance(this).registerReceiver(myBroadcastReceiver, iff);
    }

    private BroadcastReceiver myBroadcastReceiver =
            new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Log.d(LOG_TAG, "onReceive() called with: " + " intent = [" + intent + "]");
                }

            };
}
