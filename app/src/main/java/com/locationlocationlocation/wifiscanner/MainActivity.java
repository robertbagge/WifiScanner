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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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
    String wifis[];
    long scanStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();
        list = (ListView)findViewById(R.id.listView1);
        mainWifiObj = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiReciever = new WifiScanReceiver();
        mainWifiObj.startScan();
        scanStarted = System.currentTimeMillis();

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
            case R.id.action_search:
                openSearch();
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

            Log.d(TAG, "Scan finished in " + Long.toString(System.currentTimeMillis() - scanStarted) + " ms after scan started");

            List<ScanResult> wifiScanList = mainWifiObj.getScanResults();
            Log.d(TAG, "Obtain results finished in " + Long.toString(System.currentTimeMillis() - scanStarted) + " ms after scan started");
            wifis = new String[wifiScanList.size()];
            for(int i = 0; i < wifiScanList.size(); i++){
                wifis[i] = ((wifiScanList.get(i)).toString());
            }

            Log.d(TAG, "Parse results finished in " + Long.toString(System.currentTimeMillis() - scanStarted) + " ms after scan started");

            list.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
                    android.R.layout.simple_list_item_1, wifis));
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
        Toast.makeText(context, MENU_ACTION_SCAN_MESSAGE, Toast.LENGTH_SHORT).show();
    }
}
