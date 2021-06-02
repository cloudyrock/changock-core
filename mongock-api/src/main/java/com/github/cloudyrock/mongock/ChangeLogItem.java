package com.github.cloudyrock.mongock;

import java.util.List;
import java.util.Objects;

public class ChangeLogItem {

  private final Class<?> type;

  private final Object instance;

  private final String order;
  
  private final boolean failFast;
  
  private final boolean preMigration;
  
  private final boolean postMigration;

  private final List<ChangeSetItem> changeSetElements;

  public ChangeLogItem(Class<?> type, Object instance, String order, boolean failFast, boolean preMigration, boolean postMigration, List<ChangeSetItem> changeSetElements) {
    checkParameters(preMigration, postMigration);
    this.type = type;
    this.instance = instance;
    this.order = order;
    this.failFast = failFast;
    this.preMigration = preMigration;
    this.postMigration = postMigration;
    this.changeSetElements = changeSetElements;
  }

  private static void checkParameters(boolean preMigration, boolean postMigration) {
    if (preMigration && postMigration) {
      throw new IllegalArgumentException("A ChangeLog can't be defined to be executed pre and post migration.");
    }
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
  
  public boolean isPreMigration() {
    return preMigration;
  }
  
  public boolean isPostMigration() {
    return postMigration;
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

  public boolean isMigration() {
    return !isPreMigration() && !isPostMigration();
  }
}
