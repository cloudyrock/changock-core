package io.changock.runner.core.changelogs.comparator;

import io.changock.migration.api.annotations.ChangeLog;
import io.changock.migration.api.annotations.ChangeSet;
import io.changock.runner.core.util.DummyDependencyClass;

import java.util.concurrent.CountDownLatch;

@ChangeLog(order = "1")
public class Comparator1ChangeLog {


  @ChangeSet(author = "executor", id = "newChangeSet", order = "1")
  public void comparatorChangeSet1() {
  }



}
