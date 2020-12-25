package io.changock.runner.core.changelogs.withnoannotations;

import io.changock.migration.api.annotations.ChangeSet;


public class ClassWithChangeSet {

  @ChangeSet(
      author = "testUser11",
      id = "changeSet_2",
      order = "1",
      runAlways = true,
      systemVersion = "1")
  public void method_1() {
  }

  public void method() {
  }

}
