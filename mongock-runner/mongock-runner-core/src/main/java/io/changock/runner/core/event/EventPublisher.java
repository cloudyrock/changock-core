package io.changock.runner.core.event;

public interface EventPublisher {

  void publishMigrationStarted();

  void publishMigrationSuccessEvent(MigrationResult migrationResult);

  void publishMigrationFailedEvent(Exception ex);
}
