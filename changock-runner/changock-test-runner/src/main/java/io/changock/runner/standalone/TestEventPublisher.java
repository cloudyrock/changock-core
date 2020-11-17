package io.changock.runner.standalone;

import io.changock.runner.core.event.EventPublisher;
import io.changock.runner.core.event.MigrationResult;

public class TestEventPublisher implements EventPublisher {
  @Override
  public void publishMigrationSuccessEvent(MigrationResult migrationResult) {

  }

  @Override
  public void publishMigrationFailedEvent(Exception ex) {

  }
}
