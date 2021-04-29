package com.github.cloudyrock.mongock.runner.core.changelogs.postmigration;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.github.cloudyrock.mongock.PostMigration;

@PostMigration
@ChangeLog(order = "1")
public class ChangeLogPostMigration {

  @ChangeSet(author = "executor", id = "postMigration1", order = "1")
  public void postMigration1() {
    // Do nothing
  }
  
  @ChangeSet(author = "executor", id = "postMigration2", order = "2")
  public void postMigration2() {
    // Do nothing
  }
}
