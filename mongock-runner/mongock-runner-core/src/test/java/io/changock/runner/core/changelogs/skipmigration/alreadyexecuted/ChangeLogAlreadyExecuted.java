package io.changock.runner.core.changelogs.skipmigration.alreadyexecuted;

import io.changock.migration.api.annotations.ChangeLog;
import io.changock.migration.api.annotations.ChangeSet;
import io.changock.runner.core.util.DummyDependencyClass;

@ChangeLog
public class ChangeLogAlreadyExecuted {

  @ChangeSet(author = "executor", id = "alreadyExecuted", order = "1")
  public void alreadyExecuted(DummyDependencyClass dependency) {
    throw new RuntimeException("This method should not be executed, as it's supposed to be already executed");
  }

  @ChangeSet(author = "executor", id = "alreadyExecuted2", order = "2")
  public void alreadyExecuted2(DummyDependencyClass dependency) {
    throw new RuntimeException("This method should not be executed, as it's supposed to be already executed");
  }
}
