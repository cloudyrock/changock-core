package io.changock.runner.standalone;

import io.changock.runner.core.EventPublisher;

public class TestEventPublisher implements EventPublisher {
  @Override
  public void publishMigrationSuccessEvent() {

  }

  @Override
  public void publishMigrationFailedEvent(Exception ex) {

  }
}
