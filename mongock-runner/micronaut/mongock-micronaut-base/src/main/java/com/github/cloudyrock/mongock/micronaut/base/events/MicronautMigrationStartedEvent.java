package com.github.cloudyrock.mongock.micronaut.base.events;

import io.micronaut.context.event.ApplicationEvent;

public class MicronautMigrationStartedEvent extends ApplicationEvent {


  /**
   * Create a new {@code ApplicationEvent}.
   *
   * @param source the object on which the event initially occurred or with
   *               which the event is associated (never {@code null})
   */
  public MicronautMigrationStartedEvent(Object source) {
    super(source);
  }

  @Override
  public String toString() {
    return "MicronautMigrationStartedEvent{" +
        "source=" + source +
        "} " + super.toString();
  }
}
