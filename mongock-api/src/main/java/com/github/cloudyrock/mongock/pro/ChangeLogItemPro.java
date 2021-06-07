package com.github.cloudyrock.mongock.pro;

import com.github.cloudyrock.mongock.ChangeLogItem;

import java.util.List;

public class ChangeLogItemPro extends ChangeLogItem {

  public ChangeLogItemPro(Class<?> type, Object instance, String order, boolean failFast, boolean preMigration, boolean postMigration, List<ChangeSetItemPro> changeSetElements) {
    super(type, instance, order, failFast, preMigration, postMigration, changeSetElements);
  }


}
