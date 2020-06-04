package io.changock.runner.core.changelogs.multipackage.package1;

import io.changock.migration.api.annotations.ChangeLog;
import io.changock.migration.api.annotations.ChangeSet;

@ChangeLog(order = "1")
public class ChangeLogMultiPackage1 {

  @ChangeSet(author = "mongock", order = "1", id = "changeset_package1")
  public void changeSetPackage1() {
  }
}
