package io.changock.runner.base.changelogs.test1;

import io.changock.migration.api.annotations.ChangeLog;
import io.changock.migration.api.annotations.ChangeSet;

@ChangeLog(order = "2")
public class ChangeLogSuccess12 {

  @ChangeSet(
      author = "testUser12",
      id = "ChangeSet_122",
      order = "2",
      runAlways = true,
      systemVersion = "2")
  public void method_121() {
    System.out.println(ChangeLogSuccess12.class.getCanonicalName() + ".method_121()");
  }


}
