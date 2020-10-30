package io.changock.runner.core;

public interface EventPublisher {

  void publishMigrationSuccessEvent();

  void publishMigrationFailedEvent(Exception ex);
}
