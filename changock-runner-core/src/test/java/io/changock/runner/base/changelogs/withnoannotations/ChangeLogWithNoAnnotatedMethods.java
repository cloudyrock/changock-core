package io.changock.runner.base.changelogs.withnoannotations;

import io.changock.migration.api.annotations.ChangeLog;

@ChangeLog(order = "3")
public class ChangeLogWithNoAnnotatedMethods {

  public void method() {
  }

}
