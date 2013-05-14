package com.jonnyzzz.android.navy;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
* Created by Eugene Petrenko (eugene.petrenko@gmail.com)
* Date: 14.05.13 23:35
*/
public class ScanLog {
  private final List<List<ScanItem>> myItems = new ArrayList<List<ScanItem>>();

  public void addResults(@NotNull List<ScanItem> si) {
    myItems.add(new ArrayList<ScanItem>(si));
  }

  @NotNull
  public List<List<ScanItem>> getItems() {
    return myItems;
  }
}
