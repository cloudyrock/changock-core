package com.github.cloudyrock.mongock;

import java.util.List;
import java.util.Objects;

public class ChangeLogItem<CHANGESET extends ChangeSetItem> {

  private final Class<?> type;

  private final Object instance;

  private final String order;

  private final boolean failFast;

  private final List<CHANGESET> changeSetItems;
  
  private final List<CHANGESET> beforeChangeSetsItems;
  
  private final List<CHANGESET> afterChangeSetsItems;


  public ChangeLogItem(Class<?> type, Object instance, String order, boolean failFast, List<CHANGESET> changeSetElements, List<CHANGESET> beforeChangeSetsItems, List<CHANGESET> afterChangeSetsItems) {
    this.type = type;
    this.instance = instance;
    this.order = order;
    this.failFast = failFast;

    this.changeSetItems = changeSetElements;
    this.beforeChangeSetsItems = beforeChangeSetsItems;
    this.afterChangeSetsItems = afterChangeSetsItems;
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

  public boolean isFailFast() {
    return failFast;
  }

  public List<CHANGESET> getChangeSetItems() {
    return changeSetItems;
  }
  
  public List<CHANGESET> getBeforeItems() {
    return beforeChangeSetsItems;
  }

  public List<CHANGESET> getAfterItems() {
    return afterChangeSetsItems;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ChangeLogItem<?> that = (ChangeLogItem<?>) o;
    return type.equals(that.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type);
  }
}
