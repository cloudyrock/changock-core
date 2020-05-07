package io.cloudyrock.changock.runner.spring.v5.profiles.withForbiddenParameter;

import io.changock.migration.api.annotations.ChangeLog;
import io.changock.migration.api.annotations.ChangeSet;

@ChangeLog(order = "1")
public class ChangeLogWithForbiddenParameter {

  @ChangeSet(
      author = "executor",
      id = "withForbiddenParameter",
      order = "1",
      runAlways = true,
      systemVersion = "1")
  public void withForbiddenParameter(ForbiddenParameter forbiddenParameter) {
  }
}
