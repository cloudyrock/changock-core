package com.github.cloudyrock.springboot.v2_4.events;

import com.github.cloudyrock.mongock.runner.core.event.EventPublisher;
import com.github.cloudyrock.mongock.runner.core.event.MigrationResult;
import org.springframework.context.ApplicationEventPublisher;

public class SpringEventPublisher implements EventPublisher {

  private final ApplicationEventPublisher applicationEventPublisher;

  public SpringEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
    this.applicationEventPublisher = applicationEventPublisher;
  }

  @Override
  public void publishMigrationStarted() {
    runIfPublisherNotNull(() -> applicationEventPublisher.publishEvent(new SpringMigrationStartedEvent(this)));

  }

  @Override
  public void publishMigrationSuccessEvent(MigrationResult migrationResult) {
    runIfPublisherNotNull(() -> applicationEventPublisher.publishEvent(new SpringMigrationSuccessEvent(this, migrationResult)));
  }

  @Override
  public void publishMigrationFailedEvent(Exception ex) {
    runIfPublisherNotNull(() -> applicationEventPublisher.publishEvent(new SpringMigrationFailureEvent(this, ex)));
  }

  private void runIfPublisherNotNull(Runnable op) {
    if (applicationEventPublisher != null) {
      op.run();
    }
  }
}
