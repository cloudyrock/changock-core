package com.github.cloudyrock.mongock.runner.core.changelogs.premigration;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.github.cloudyrock.mongock.PreMigration;

@PreMigration
@ChangeLog(order = "5")
public class ChangeLogPreMigration {

  @ChangeSet(author = "executor", id = "preMigration1", order = "1")
  public void preMigration1() {
    // Do nothing
  }
  
  @ChangeSet(author = "executor", id = "preMigration2", order = "2")
  public void preMigration2() {
    // Do nothing
  }
}
