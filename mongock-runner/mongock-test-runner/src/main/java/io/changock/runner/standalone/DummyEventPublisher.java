package io.changock.runner.standalone;

import io.changock.runner.core.event.EventPublisher;
import io.changock.runner.core.event.MigrationResult;

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
