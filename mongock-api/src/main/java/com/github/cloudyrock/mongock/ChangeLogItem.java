package com.github.cloudyrock.mongock;

import java.util.List;

public class ChangeLogItem extends ChangeLogItemBase {
  private final List<ChangeSetItem> changeSetItems;

  public ChangeLogItem(Class<?> type, Object instance, String order, boolean failFast, boolean preMigration, boolean postMigration, List<ChangeSetItem> changeSetElements) {
    super(type, instance, order, failFast, preMigration, postMigration);
    this.changeSetItems = changeSetElements;
  }

  @Override
  public List<ChangeSetItem> getChangeSetElements() {
    return changeSetItems;
  }
}
