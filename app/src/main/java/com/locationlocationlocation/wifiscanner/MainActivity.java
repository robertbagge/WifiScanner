package com.locationlocationlocation.wifiscanner;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";

    private final static String MENU_ACTION_SEARCH_MESSAGE = "Search not implemented";
    private final static String MENU_ACTION_SCAN_MESSAGE = "Scan not implemented";
    private final static String MENU_ACTION_SAVE_MESSAGE = "Save not implemented";
    private final static String MENU_ACTION_SETTINGS_MESSAGE = "Settings not implemented";

    private Context context;

    WifiManager mainWifiObj;
    WifiScanReceiver wifiReciever;
    ListView list;
    List<ScanResult> wifiScanList;
    long scanStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();
        list = (ListView)findViewById(R.id.listView1);
        mainWifiObj = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiReciever = new WifiScanReceiver();

    }

    protected void onPause() {
        unregisterReceiver(wifiReciever);
        super.onPause();
    }

    protected void onResume() {
        registerReceiver(wifiReciever, new IntentFilter(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        super.onResume();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_play:
                startNewScan();
                return true;
            case R.id.action_save_icon:
                saveResults();
                return true;
            case R.id.action_settings:
                openSettings();
                return true;
            case R.id.action_save:
                saveResults();
                return true;
            case R.id.action_scan:
                startNewScan();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    class WifiScanReceiver extends BroadcastReceiver {
        @SuppressLint("UseValueOf")
        public void onReceive(Context c, Intent intent) {

            Log.d(TAG, "Scan finished after: " + Long.toString(System.currentTimeMillis() - scanStarted) + "ms");
            wifiScanList = mainWifiObj.getScanResults();
            list.setAdapter(new ScanResultsAdapter(context, wifiScanList));
        }
    }

    private void openSearch(){
        Toast.makeText(context, MENU_ACTION_SEARCH_MESSAGE, Toast.LENGTH_SHORT).show();
    }

    private void openSettings(){
        Toast.makeText(context, MENU_ACTION_SETTINGS_MESSAGE, Toast.LENGTH_SHORT).show();
    }

    private void saveResults(){
        Toast.makeText(context, MENU_ACTION_SAVE_MESSAGE, Toast.LENGTH_SHORT).show();
    }

    private void startNewScan(){
        mainWifiObj.startScan();
        scanStarted = System.currentTimeMillis();
        wifiScanList = null;
        Log.d(TAG, "Scan started");
    }

    private class ScanResultsAdapter extends ArrayAdapter<ScanResult> {
        public ScanResultsAdapter(Context context, List<ScanResult> scanResults) {
            super(context, 0, scanResults);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            ScanResult scanResult = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_scan_result, parent, false);
            }
            // Lookup view for data population
            TextView tvBSSID = (TextView) convertView.findViewById(R.id.bssid);
            TextView tvSSID = (TextView) convertView.findViewById(R.id.ssid);
            TextView tvRSSI = (TextView) convertView.findViewById(R.id.rssi);

            // Populate the data into the template view using the data object
            tvBSSID.setText(scanResult.BSSID);
            tvSSID.setText(scanResult.SSID);
            int level = scanResult.level;
            tvRSSI.setText(Integer.toString(level) + " dB");

            if(level > -50){
                tvRSSI.setTextColor(getResources().getColor(R.color.flat_ui_turqouise));
            }else if(level > -70){
                tvRSSI.setTextColor(getResources().getColor(R.color.flat_ui_sun_flower));
            }else{
                tvRSSI.setTextColor(getResources().getColor(R.color.flat_ui_pumpkin));
            }
            // Return the completed view to render on screen
            return convertView;
        }
    }
}
