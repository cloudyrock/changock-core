package io.changock.runner.spring.util.events;

import io.changock.runner.core.event.ChangockEvent;
import io.changock.runner.core.event.MigrationResult;
import org.springframework.context.ApplicationEvent;

public class SpringMigrationSuccessEvent extends ApplicationEvent implements ChangockEvent {

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
