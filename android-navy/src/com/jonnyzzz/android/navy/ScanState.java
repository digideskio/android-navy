package com.jonnyzzz.android.navy;

import android.net.wifi.ScanResult;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 17.05.13 22:01
 */
public class ScanState {
  private final Map<String, Integer> myState = new TreeMap<String, Integer>();

  public void add(@NotNull final String bssid, int level) {
    myState.put(bssid, level);
  }

  public void add(@NotNull ScanResult scanResult) {
    myState.put(scanResult.BSSID, scanResult.level);
  }

  @NotNull
  public static Collection<ScanViewItem> toItems(@NotNull final ScanState actual, @NotNull final ScanState stored) {
    final Set<String> keys = new TreeSet<String>();
    keys.addAll(actual.myState.keySet());
    keys.addAll(stored.myState.keySet());

    List<ScanViewItem> result = new ArrayList<ScanViewItem>(keys.size());
    for (String key : keys) {
      final Integer ac = actual.myState.get(key);
      final Integer st = stored.myState.get(key);

      result.add(new ScanViewItem(key, st, ac));
    }
    return result;
  }
}
