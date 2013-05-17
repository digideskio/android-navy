package com.jonnyzzz.android.navy;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 17.05.13 21:57
 */
public class ScanViewItem {
  private final String myBSSID;
  private final Integer myRecordedLevel;
  private final Integer myActualLevel;

  public ScanViewItem(@NotNull String BSSID, Integer recordedLevel, Integer actualLevel) {
    myBSSID = BSSID;
    myRecordedLevel = recordedLevel;
    myActualLevel = actualLevel;
  }

  @NotNull
  public String getBSSID() {
    return myBSSID;
  }

  @Nullable
  public Integer getRecordedLevel() {
    return myRecordedLevel;
  }

  @Nullable
  public Integer getActualLevel() {
    return myActualLevel;
  }

  public boolean isMatch() {
    if (myActualLevel == null) return false;
    if (myRecordedLevel == null) return false;
    return Math.abs(myActualLevel - myRecordedLevel) < 3;
  }
}
