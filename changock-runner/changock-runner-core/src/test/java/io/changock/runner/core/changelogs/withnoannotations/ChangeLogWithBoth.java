package io.changock.runner.core.changelogs.withnoannotations;

import io.changock.migration.api.annotations.ChangeLog;
import io.changock.migration.api.annotations.ChangeSet;

@ChangeLog(order = "2")
public class ChangeLogWithBoth {

  @ChangeSet(
      author = "testUser11",
      id = "changeSet_1",
      order = "2",
      runAlways = true,
      systemVersion = "1")
  public void method_1() {
  }

  public void method_2() {
  }

}
