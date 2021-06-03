package com.github.cloudyrock.mongock.pro;

import com.github.cloudyrock.mongock.ChangeSetItem;

import java.lang.reflect.Method;

public class ChangeSetItemPro extends ChangeSetItem {
  public ChangeSetItemPro(String id, String author, String order, boolean runAlways, String systemVersion, boolean failFast, Method method) {
    super(id, author, order, runAlways, systemVersion, failFast, method);
  }
}
