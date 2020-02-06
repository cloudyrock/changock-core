package io.changock.driver.api.changelog;

import java.util.List;

public class ChangeLogItem {

  private Class<?> type;

  private Object instance;

  private String order;

  private List<ChangeSetItem> changeSetElements;

  public ChangeLogItem(Class<?> type, Object instance, String order, List<ChangeSetItem> changeSetElements) {
    this.type = type;
    this.instance = instance;
    this.order = order;
    this.changeSetElements = changeSetElements;
  }


  public Class<?> getType() {
    return type;
  }

  public Object getInstance() {
    return instance;
  }

  public String getOrder() {
    return order;
  }

  public List<ChangeSetItem> getChangeSetElements() {
    return changeSetElements;
  }
}
