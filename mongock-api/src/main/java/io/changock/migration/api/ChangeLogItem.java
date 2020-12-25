package io.changock.migration.api;

import java.util.List;
import java.util.Objects;

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


  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ChangeLogItem)) return false;
    ChangeLogItem that = (ChangeLogItem) o;
    return type.equals(that.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type);
  }
}
