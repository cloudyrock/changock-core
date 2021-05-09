package com.github.cloudyrock.springboot.v2_2.base.events;

import com.github.cloudyrock.mongock.runner.core.event.MigrationResult;
import com.github.cloudyrock.mongock.runner.core.event.MongockEvent;
import org.springframework.context.ApplicationEvent;

public class SpringMigrationSuccessEvent extends ApplicationEvent implements MongockEvent {

  private final MigrationResult migrationResult;

  /**
   * Create a new {@code ApplicationEvent}.
   *
   * @param source the object on which the event initially occurred or with
   *               which the event is associated (never {@code null})
   */
  public SpringMigrationSuccessEvent(Object source, MigrationResult migrationResult) {
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
