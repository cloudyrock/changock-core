package io.changock.runner.base.changelogs.test1;

import io.changock.migration.api.annotations.ChangeLog;
import io.changock.migration.api.annotations.ChangeSet;

@ChangeLog(order = "1")
public class ChangeLogSuccess11 {

  @ChangeSet(
      author = "testUser11",
      id = "ChangeSet_121",
      order = "1",
      runAlways = true,
      systemVersion = "1")
  public void method_111() {
  }


}
