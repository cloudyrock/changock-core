package io.changock.runner.spring.util;

import io.changock.runner.core.EventPublisher;
import io.changock.runner.spring.util.events.MongockMigrationFailEvent;
import io.changock.runner.spring.util.events.MongockMigrationSuccessEvent;
import org.springframework.context.ApplicationEventPublisher;

public class SpringEventPublisher implements EventPublisher {

  private final ApplicationEventPublisher applicationEventPublisher;

  public SpringEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
    this.applicationEventPublisher = applicationEventPublisher;
  }

  @Override
  public void publishMigrationSuccessEvent() {
    if (applicationEventPublisher != null) {
      applicationEventPublisher.publishEvent(new MongockMigrationSuccessEvent(this));
    }
  }

  @Override
  public void publishMigrationFailedEvent(Exception ex) {
    if (applicationEventPublisher != null) {
      applicationEventPublisher.publishEvent(new MongockMigrationFailEvent(this, ex));
    }
  }
}
