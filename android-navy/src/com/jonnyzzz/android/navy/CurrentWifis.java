package com.jonnyzzz.android.navy;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CurrentWifis extends Activity {
  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    final WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
    if (!wifi.isWifiEnabled()) {
      Toast.makeText(getApplicationContext(), "wifi is disabled... making it enabled", Toast.LENGTH_LONG).show();
      wifi.setWifiEnabled(true);
    }

    final ListView lv = (ListView) findViewById(R.id.main_wifis_list);
    final List<String> detectedWifis = new ArrayList<String>();
    final ArrayAdapter<String> adapter = new ArrayAdapter<String>(CurrentWifis.this, android.R.layout.simple_list_item_1, detectedWifis);
    lv.setAdapter(adapter);
    registerReceiver(new BroadcastReceiver() {
      @Override
      public void onReceive(Context c, Intent intent) {
        final List<ScanResult> results = wifi.getScanResults();
        detectedWifis.clear();
        for (ScanResult result : results) {
          detectedWifis.add(result.SSID + " -> " + result.BSSID + " -> " + result.level);
        }
        Collections.sort(detectedWifis);
        adapter.notifyDataSetChanged();
        wifi.startScan();
      }
    }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

    findViewById(R.id.main_share_scan).setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        wifi.startScan();
      }
    });
  }
}
