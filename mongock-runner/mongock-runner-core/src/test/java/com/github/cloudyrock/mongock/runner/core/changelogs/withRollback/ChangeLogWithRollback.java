package com.github.cloudyrock.mongock.runner.core.changelogs.withRollback;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.github.cloudyrock.mongock.Rollback;

import java.util.concurrent.CountDownLatch;

@ChangeLog(order = "1")
public class ChangeLogWithRollback {

  public final static CountDownLatch rollbackCalledLatch = new CountDownLatch(1);


  @ChangeSet(
      author = "mongock_test",
      id = "changeset_with_rollback_1",
      order = "1",
      systemVersion = "1")
  public void changeSetFailing1() {
    if(true) throw new RuntimeException("Expected exception in " + ChangeLogWithRollback.class + " changeLog");
  }

  @Rollback("changeset_with_rollback_1")
  public void rollbackChangeSetFailing1() {
    rollbackCalledLatch.countDown();
  }


}
