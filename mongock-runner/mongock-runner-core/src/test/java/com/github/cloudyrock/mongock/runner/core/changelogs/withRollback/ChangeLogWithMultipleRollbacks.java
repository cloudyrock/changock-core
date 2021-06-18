package com.github.cloudyrock.mongock.runner.core.changelogs.withRollback;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.github.cloudyrock.mongock.Rollback;

@ChangeLog(order = "1")
public class ChangeLogWithMultipleRollbacks {

  @ChangeSet(
      author = "mongock_test",
      id = "changeset_with_multiple_rollbacks",
      order = "1",
      systemVersion = "1")
  public void changeSetWithMultipleRollbacks() {
    if(true) throw new RuntimeException("Expected exception in " + ChangeLogWithMultipleRollbacks.class + " changeLog");
  }

  @Rollback("changeset_with_multiple_rollbacks")
  public void rollbackChangeSetFailing1() {

  }


  @Rollback("changeset_with_multiple_rollbacks")
  public void rollbackChangeSetFailing1_2() {

  }


}
