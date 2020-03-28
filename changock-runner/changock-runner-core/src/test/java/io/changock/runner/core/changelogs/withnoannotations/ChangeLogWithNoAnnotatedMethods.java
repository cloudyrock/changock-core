package io.changock.runner.core.changelogs.withnoannotations;

import io.changock.migration.api.annotations.ChangeLog;

@ChangeLog(order = "3")
public class ChangeLogWithNoAnnotatedMethods {

  public void method() {
  }

}
