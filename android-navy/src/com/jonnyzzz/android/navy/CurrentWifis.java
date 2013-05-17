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
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

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
    final List<ScanViewItem> detectedWifis = new ArrayList<ScanViewItem>();
    final ScanLog myLog = new ScanLog();
    final AtomicReference<ScanState> myPersistedState = new AtomicReference<ScanState>(new ScanState());
    final AtomicBoolean myScanRunning = new AtomicBoolean(false);
    final AtomicBoolean myShowDiff = new AtomicBoolean(false);

    final ArrayAdapter<ScanViewItem> adapter = new ArrayAdapter<ScanViewItem>(CurrentWifis.this, R.layout.list_item, detectedWifis) {
      @Override
      public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
          row = CurrentWifis.this.getLayoutInflater().inflate(R.layout.list_item, parent, false);
        }
        final ScanViewItem item = detectedWifis.get(position);
        final TextView bssid = (TextView) row.findViewById(R.id.listItem_BSSID);
        final TextView level = (TextView) row.findViewById(R.id.listItem_Level);
        final TextView diff = (TextView) row.findViewById(R.id.listItem_selected_Level);

        bssid.setText(item.getBSSID());
        level.setText(n(item.getActualLevel()));
        diff.setText(n(item.getRecordedLevel()));
        diff.setVisibility(!myShowDiff.get() || item.getRecordedLevel() == null ? View.INVISIBLE : View.VISIBLE);
        level.setVisibility(item.getActualLevel() == null ? View.INVISIBLE : View.VISIBLE);

        level.setBackgroundResource(item.isMatch() ? R.color.wifi_list_actual_bg_matching : R.color.wifi_list_actual_bg_default);
        diff.setBackgroundResource(item.isMatch() ? R.color.wifi_list_stored_bg_matching : R.color.wifi_list_stored_bg_default);
        return row;
      }

      @NotNull
      private String n(Object o) {
        if (o == null) return "-\u221E";
        return o.toString();
      }
    };

    lv.setAdapter(adapter);
    registerReceiver(new BroadcastReceiver() {
      @Override
      public void onReceive(Context c, Intent intent) {
        final List<ScanResult> results = wifi.getScanResults();
        final ScanState ss = new ScanState();
        for (ScanResult result : results) {
          ss.add(result);
        }

        detectedWifis.clear();
        detectedWifis.addAll(ScanState.toItems(ss, myPersistedState.get()));
        adapter.notifyDataSetChanged();
        myLog.addResults(ss);

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

    findViewById(R.id.main_store).setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        myShowDiff.set(true);
        myPersistedState.set(myLog.getLatestState());
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
