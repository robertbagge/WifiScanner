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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
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
    LinearLayout linlaHeaderProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();
        list = (ListView)findViewById(R.id.listView1);
        mainWifiObj = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiReciever = new WifiScanReceiver();
        linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress);
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
            case R.id.action_exit:
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
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
            linlaHeaderProgress.setVisibility(View.GONE);
            list.setVisibility(View.VISIBLE);

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
        String csv = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/test/test.csv";
        CSVWriter writer = null;
        try {
            writer = new CSVWriter(new FileWriter(csv), ',');
            List<String[]> data = new ArrayList<String[]>();
            data.add(new String[] {"India", "New Delhi"});
            data.add(new String[] {"United States", "Washington D.C"});
            data.add(new String[] {"Germany", "Berlin"});
            writer.writeAll(data);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, csv);
        Toast.makeText(context, MENU_ACTION_SAVE_MESSAGE, Toast.LENGTH_SHORT).show();
    }

    private void startNewScan(){
        mainWifiObj.startScan();
        scanStarted = System.currentTimeMillis();
        wifiScanList = null;
        Log.d(TAG, "Scan started");
        linlaHeaderProgress.setVisibility(View.VISIBLE);
        list.setVisibility(View.GONE);
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
            TextView tvFrequency = (TextView) convertView.findViewById(R.id.frequency);

            // Populate the data into the template view using the data object
            tvBSSID.setText(scanResult.BSSID);
            tvSSID.setText(scanResult.SSID);
            int level = scanResult.level;
            tvRSSI.setText(Integer.toString(level) + " dB");

            int frequency = scanResult.frequency;
            String frequencyStr = "?? Hz";
            if(frequency > 2000 && frequency < 3000) {
                frequencyStr = "2.4 GHz";

            }
            else if(frequency > 4500 && frequency < 5500){
                frequencyStr = "5 GHz";
            }
            tvFrequency.setText(frequencyStr);


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
