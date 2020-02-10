package io.changock.runner.core.changelogs.executor.test2;

import io.changock.migration.api.annotations.ChangeLog;
import io.changock.migration.api.annotations.ChangeSet;
import io.changock.runner.core.util.DummyDependencyClass;

@ChangeLog(order = "0")
public class ExecutorChangeLog2 {


  @ChangeSet(author = "executor", id = "newChangeSet", order = "1")
  public void newChangeSet(DummyDependencyClass dependency) {
  }




}
