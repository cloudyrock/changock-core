package com.github.cloudyrock.mongock.runner.core.changelogs.prepostmigration;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.github.cloudyrock.mongock.PostMigration;
import com.github.cloudyrock.mongock.PreMigration;

@PreMigration
@PostMigration
@ChangeLog(order = "1")
public class ChangeLogPrePostMigration {

  @ChangeSet(author = "executor", id = "prePostMigration1", order = "1")
  public void prePostMigration1() {
    throw new RuntimeException("This method should not be executed.");
  }
}
