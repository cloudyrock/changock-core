package com.github.cloudyrock.mongock;

import java.util.List;
import java.util.Objects;

public class ChangeLogItem {

  private final Class<?> type;

  private final Object instance;

  private final String order;
  
  private final boolean failFast;
  
  private final boolean preTransaction;
  
  private final boolean postTransaction;

  private final List<ChangeSetItem> changeSetElements;

  public ChangeLogItem(Class<?> type, Object instance, String order, boolean failFast, boolean preTransaction, boolean postTransaction, List<ChangeSetItem> changeSetElements) {
    this.type = type;
    this.instance = instance;
    this.order = order;
    this.failFast = failFast;
    this.preTransaction = preTransaction;
    this.postTransaction = postTransaction;
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
  
  public boolean isFailFast() {
    return failFast;
  }
  
  public boolean isPreTransaction() {
    return preTransaction;
  }
  
  public boolean isPostTransaction() {
    return postTransaction;
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
