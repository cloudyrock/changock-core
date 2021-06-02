package com.github.cloudyrock.mongock;

import java.util.List;

public class ChangeLogItem extends ChangeLogItemBase<ChangeSetItem> {
  public ChangeLogItem(Class<?> type, Object instance, String order, boolean failFast, boolean preMigration, boolean postMigration, List<ChangeSetItem> changeSetElements) {
    super(type, instance, order, failFast, preMigration, postMigration, changeSetElements);
  }
}
