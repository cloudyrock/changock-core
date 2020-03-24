package io.changock.runner.core.changelogs.executor.test3_with_nonFailFast;

import io.changock.migration.api.annotations.ChangeLog;
import io.changock.migration.api.annotations.ChangeSet;
import io.changock.runner.core.util.DummyDependencyClass;

import java.util.concurrent.CountDownLatch;

@ChangeLog(order = "0")
public class ExecutorWithNonFailFastChangeLog {

  public final static  CountDownLatch latch = new CountDownLatch(3);

  @ChangeSet(author = "executor", id = "newChangeSet1", order = "1")
  public void newChangeSet1(DummyDependencyClass dependency) {
    latch.countDown();
  }


  @ChangeSet(author = "executor", id = "changeSetNonFailFast", order = "2", failFast = false)
  public void changeSetNonFailFast(DummyDependencyClass dependency) {
    latch.countDown();
    throw new RuntimeException("Exception from changeSetNonFailFast");
  }

  @ChangeSet(author = "executor", id = "newChangeSet2", order = "2")
  public void newChangeSet2(DummyDependencyClass dependency) {
    latch.countDown();
  }




}
