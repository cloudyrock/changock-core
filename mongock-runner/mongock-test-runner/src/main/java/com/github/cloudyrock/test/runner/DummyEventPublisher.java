package com.github.cloudyrock.test.runner;

import com.github.cloudyrock.mongock.runner.core.event.EventPublisher;
import com.github.cloudyrock.mongock.runner.core.event.MigrationResult;

public class DummyEventPublisher implements EventPublisher {
  @Override
  public void publishMigrationStarted() {

  }

  @Override
  public void publishMigrationSuccessEvent(MigrationResult migrationResult) {

  }

  @Override
  public void publishMigrationFailedEvent(Exception ex) {

  }
}
