package io.changock.runner.spring.util;

import io.changock.runner.core.EventPublisher;
import io.changock.runner.spring.util.events.DbMigrationFailEvent;
import io.changock.runner.spring.util.events.DbMigrationSuccessEvent;
import org.springframework.context.ApplicationEventPublisher;

public class SpringEventPublisher implements EventPublisher {

  private final ApplicationEventPublisher applicationEventPublisher;

  public SpringEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
    this.applicationEventPublisher = applicationEventPublisher;
  }

  @Override
  public void publishMigrationSuccessEvent() {
    if (applicationEventPublisher != null) {
      applicationEventPublisher.publishEvent(new DbMigrationSuccessEvent(this));
    }
  }

  @Override
  public void publishMigrationFailedEvent(Exception ex) {
    if (applicationEventPublisher != null) {
      applicationEventPublisher.publishEvent(new DbMigrationFailEvent(this, ex));
    }
  }
}
