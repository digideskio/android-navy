package com.jonnyzzz.android.navy;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;

/**
* Created by Eugene Petrenko (eugene.petrenko@gmail.com)
* Date: 14.05.13 23:35
*/
public class ScanLog {
  private final long myStartTime = now();
  private final Map<Integer, ScanState> myItems = new LinkedHashMap<Integer, ScanState>();
  private ScanState myLatestState = new ScanState();

  public void addResults(@NotNull ScanState si) {
    myItems.put((int) (now() - myStartTime), myLatestState = si);
  }

  @NotNull
  public ScanState getLatestState() {
    return myLatestState;
  }

  private static long now() {
    return System.currentTimeMillis();
  }

  public void reset() {
    myItems.clear();
  }
}
