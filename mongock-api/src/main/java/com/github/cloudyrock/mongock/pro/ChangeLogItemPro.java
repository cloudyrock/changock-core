package com.github.cloudyrock.mongock.pro;

import com.github.cloudyrock.mongock.ChangeLogItemBase;

import java.util.List;

public class ChangeLogItemPro extends ChangeLogItemBase {
  private final List<ChangeSetItemPro> changeSetItems;

  public ChangeLogItemPro(Class<?> type, Object instance, String order, boolean failFast, boolean preMigration, boolean postMigration, List<ChangeSetItemPro> changeSetElements) {
    super(type, instance, order, failFast, preMigration, postMigration);
    this.changeSetItems = changeSetElements;
  }

  @Override
  public List<ChangeSetItemPro> getChangeSetElements() {
    return changeSetItems;
  }

}
