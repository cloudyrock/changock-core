package io.changock.runner.spring.util.events;

import org.springframework.context.ApplicationEvent;

public class DbMigrationFailEvent extends ApplicationEvent {
  private final Exception exception;

  /**
   * Create a new {@code ApplicationEvent}.
   *
   * @param source the object on which the event initially occurred or with
   *               which the event is associated (never {@code null})
   */
  public DbMigrationFailEvent(Object source, Exception ex) {
    super(source);
    this.exception = ex;
  }

  public Exception getException() {
    return exception;
  }

  @Override
  public String toString() {
    return "MongockMigrationFailEvent{" +
        "exception=" + exception +
        "} " + super.toString();
  }
}
