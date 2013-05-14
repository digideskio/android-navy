package com.jonnyzzz.android.navy;

import org.jetbrains.annotations.NotNull;

/**
* Created by Eugene Petrenko (eugene.petrenko@gmail.com)
* Date: 14.05.13 23:35
*/
public class ScanItem implements Comparable<ScanItem> {
  private final String myBSSID;
  private final int myLevel;

  public ScanItem(@NotNull String BSSID, int level) {
    myBSSID = BSSID;
    myLevel = level;
  }

  @NotNull
  public String getBSSID() {
    return myBSSID;
  }

  public int getLevel() {
    return myLevel;
  }

  public int compareTo(@NotNull final ScanItem scanItem) {
    return myBSSID.compareTo(scanItem.getBSSID());
  }
}
