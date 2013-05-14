package com.jonnyzzz.android.navy;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
* Created by Eugene Petrenko (eugene.petrenko@gmail.com)
* Date: 14.05.13 23:35
*/
public class ScanLog {
  private final long myStartTime = now();
  private final Map<Integer, List<ScanItem>> myItems = new LinkedHashMap<Integer, List<ScanItem>>();

  public void addResults(@NotNull List<ScanItem> si) {
    myItems.put((int) (now() - myStartTime), new ArrayList<ScanItem>(si));
  }

  @NotNull
  public Map<Integer, List<ScanItem>> getItems() {
    return myItems;
  }

  private static long now() {
    return System.currentTimeMillis();
  }

  public void reset() {
    myItems.clear();
  }
}
