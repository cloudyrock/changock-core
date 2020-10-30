package io.changock.runner.spring.util.events;

import org.springframework.context.ApplicationEvent;

public class MongockMigrationSuccessEvent extends ApplicationEvent {

  /**
   * Create a new {@code ApplicationEvent}.
   *
   * @param source the object on which the event initially occurred or with
   *               which the event is associated (never {@code null})
   */
  public MongockMigrationSuccessEvent(Object source) {
    super(source);
  }
}
