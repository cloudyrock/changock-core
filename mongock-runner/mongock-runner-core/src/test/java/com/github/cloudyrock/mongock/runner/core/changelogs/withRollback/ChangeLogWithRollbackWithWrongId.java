package com.github.cloudyrock.mongock.runner.core.changelogs.withRollback;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.github.cloudyrock.mongock.Rollback;

@ChangeLog(order = "1")
public class ChangeLogWithRollbackWithWrongId {

  @ChangeSet(
      author = "mongock_test",
      id = "changeset_with_rollback_1",
      order = "1",
      systemVersion = "1")
  public void changeSetFailing1() {
    if(true) throw new RuntimeException("Expected exception in " + ChangeLogWithRollbackWithWrongId.class + " changeLog");
  }

  @Rollback("wrong_id")
  public void rollbackChangeSetFailing1() {

  }


}
