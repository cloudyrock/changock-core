package com.github.cloudyrock.springboot.base.events;

import com.github.cloudyrock.mongock.runner.core.event.MongockResultEvent;
import com.github.cloudyrock.mongock.runner.core.event.result.MigrationFailedResult;
import com.github.cloudyrock.mongock.runner.core.event.result.MigrationResult;
import org.springframework.context.ApplicationEvent;

public class SpringMigrationFailureEvent extends ApplicationEvent implements MongockResultEvent {
  private final MigrationFailedResult migrationResult;

  /**
   * Create a new {@code ApplicationEvent}.
   *
   * @param source the object on which the event initially occurred or with
   *               which the event is associated (never {@code null})
   */
  public SpringMigrationFailureEvent(Object source, Exception ex) {
    super(source);
    migrationResult = MigrationResult.failedResult(ex);
  }


  @Override
  public MigrationFailedResult getMigrationResult() {
    return migrationResult;
  }

  @Override
  public String toString() {
    return "SpringMigrationFailureEvent{" +
        "migrationResult=" + migrationResult +
        ", source=" + source +
        "} " + super.toString();
  }
}
