package com.github.cloudyrock.mongock.micronaut.base.events;

import com.github.cloudyrock.mongock.runner.core.event.MongockResultEvent;
import com.github.cloudyrock.mongock.runner.core.event.result.MigrationFailedResult;
import io.micronaut.context.event.ApplicationEvent;

public class MicronautMigrationFailureEvent extends ApplicationEvent implements MongockResultEvent {
  private final MigrationFailedResult migrationResult;

  /**
   * Create a new {@code ApplicationEvent}.
   *
   * @param source the object on which the event initially occurred or with
   *               which the event is associated (never {@code null})
   */
  public MicronautMigrationFailureEvent(Object source, Exception ex) {
    super(source);
    migrationResult = new MigrationFailedResult(ex);
  }

  @Override
  public MigrationFailedResult getMigrationResult() {
    return migrationResult;
  }

  @Override
  public String toString() {
    return "MicronautMigrationFailureEvent{" +
        "migrationResult=" + migrationResult +
        ", source=" + source +
        "} " + super.toString();
  }
}
