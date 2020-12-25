package io.changock.runner.core.changelogs.comparator;

import io.changock.migration.api.annotations.ChangeLog;
import io.changock.migration.api.annotations.ChangeSet;

@ChangeLog(order = "1")
public class Comparator2ChangeLog {


  @ChangeSet(author = "executor", id = "newChangeSet", order = "1")
  public void comparatorChangeSet1() {
  }



}
