package io.changock.runner.base.changelogs.withnoannotations;

import io.changock.migration.api.annotations.ChangeLog;
import io.changock.migration.api.annotations.ChangeSet;

@ChangeLog(order = "1")
public class ChangeLogNormal {

  @ChangeSet(
      author = "testUser11",
      id = "changeSet_0",
      order = "1",
      runAlways = true,
      systemVersion = "1")
  public void method_1() {
  }
}
