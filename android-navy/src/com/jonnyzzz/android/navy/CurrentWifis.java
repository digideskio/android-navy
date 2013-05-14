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
import android.view.ViewGroup;
import android.widget.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

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
    final List<ScanItem> detectedWifis = new ArrayList<ScanItem>();
    final ScanLog myLog = new ScanLog();
    final AtomicBoolean myScanRunning = new AtomicBoolean(false);

    final ArrayAdapter<ScanItem> adapter = new ArrayAdapter<ScanItem>(CurrentWifis.this, R.layout.list_item, detectedWifis) {
      @Override
      public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
          row = CurrentWifis.this.getLayoutInflater().inflate(R.layout.list_item, parent, false);
        }
        final ScanItem item = detectedWifis.get(position);
        ((TextView) row.findViewById(R.id.listItem_BSSID)).setText(item.getBSSID());
        ((TextView) row.findViewById(R.id.listItem_Level)).setText("" + item.getLevel());
        return row;
      }
    };

    lv.setAdapter(adapter);
    registerReceiver(new BroadcastReceiver() {
      @Override
      public void onReceive(Context c, Intent intent) {
        final List<ScanResult> results = wifi.getScanResults();
        detectedWifis.clear();
        for (ScanResult result : results) {
          detectedWifis.add(new ScanItem(result.BSSID, result.level));
        }
        Collections.sort(detectedWifis);
        adapter.notifyDataSetChanged();
        myLog.addResults(detectedWifis);
        if (myScanRunning.get()) {
          wifi.startScan();
        }
      }
    }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

    findViewById(R.id.main_share_scan).setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        myScanRunning.set(!myScanRunning.get());
        if (myScanRunning.get()) {
          wifi.startScan();
        }
        updateScanButton(myScanRunning.get());
      }
    });

    findViewById(R.id.main_share_button).setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{"wifi@jonnyzzz.name"});
        i.putExtra(Intent.EXTRA_SUBJECT, "[scan]");
        i.setType("plain/text");
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        i.putExtra(Intent.EXTRA_TEXT, "Data:\r\n" + gson.toJson(myLog));
        myLog.reset();

        myScanRunning.set(false);
        updateScanButton(false);

        view.getContext().startActivity(i);
      }
    });
  }

  private void updateScanButton(boolean scanning) {
    final Button bt = (Button) findViewById(R.id.main_share_scan);
    if (scanning) {
      bt.setText("Stop Scan!");
    } else {
      bt.setText("Re-Start Scan!");
    }
  }
}
