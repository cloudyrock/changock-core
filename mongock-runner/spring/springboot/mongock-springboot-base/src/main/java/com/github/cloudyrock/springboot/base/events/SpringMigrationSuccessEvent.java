package com.github.cloudyrock.springboot.base.events;

import com.github.cloudyrock.mongock.runner.core.event.MongockResultEvent;
import com.github.cloudyrock.mongock.runner.core.event.result.MigrationResult;
import com.github.cloudyrock.mongock.runner.core.event.result.MigrationSuccessResult;
import org.springframework.context.ApplicationEvent;

public class SpringMigrationSuccessEvent<R> extends ApplicationEvent implements MongockResultEvent {

  private final MigrationSuccessResult<R> migrationResult;

  /**
   * Create a new {@code ApplicationEvent}.
   *
   * @param source the object on which the event initially occurred or with
   *               which the event is associated (never {@code null})
   */
  public SpringMigrationSuccessEvent(Object source, MigrationSuccessResult<R> migrationResult) {
    super(source);
    this.migrationResult = migrationResult;
  }

  @Override
  public MigrationResult getMigrationResult() {
    return migrationResult;
  }

  @Override
  public String toString() {
    return "SpringMigrationSuccessEvent{" +
        "migrationResult=" + migrationResult +
        "} " + super.toString();
  }
}
