package io.changock.runner.spring.util;

import io.changock.runner.core.event.EventPublisher;
import io.changock.runner.core.event.MigrationResult;
import io.changock.runner.spring.util.events.SpringMigrationFailureEvent;
import io.changock.runner.spring.util.events.SpringMigrationSuccessEvent;
import org.springframework.context.ApplicationEventPublisher;

public class SpringEventPublisher implements EventPublisher {

  private final ApplicationEventPublisher applicationEventPublisher;

  public SpringEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
    this.applicationEventPublisher = applicationEventPublisher;
  }

  @Override
  public void publishMigrationSuccessEvent(MigrationResult migrationResult) {
    if (applicationEventPublisher != null) {
      applicationEventPublisher.publishEvent(new SpringMigrationSuccessEvent(this, migrationResult));
    }
  }

  @Override
  public void publishMigrationFailedEvent(Exception ex) {
    if (applicationEventPublisher != null) {
      applicationEventPublisher.publishEvent(new SpringMigrationFailureEvent(this, ex));
    }
  }
}
