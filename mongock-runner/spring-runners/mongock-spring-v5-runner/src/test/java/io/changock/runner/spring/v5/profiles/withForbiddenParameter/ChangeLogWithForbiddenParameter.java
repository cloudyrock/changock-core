package io.changock.runner.spring.v5.profiles.withForbiddenParameter;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;

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
