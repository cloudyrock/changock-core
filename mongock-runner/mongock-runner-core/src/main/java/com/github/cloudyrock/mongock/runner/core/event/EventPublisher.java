package com.github.cloudyrock.mongock.runner.core.event;

import com.github.cloudyrock.mongock.runner.core.event.result.MigrationResult;

public interface EventPublisher {

  static EventPublisher empty() {
    return new EventPublisher() {
      @Override
      public void publishMigrationStarted() {
      }

      @Override
      public void publishMigrationSuccessEvent(MigrationResult migrationResult) {
      }

      @Override
      public void publishMigrationFailedEvent(Exception ex) {

      }
    };
  }

  void publishMigrationStarted();

  void publishMigrationSuccessEvent(MigrationResult migrationResult);

  void publishMigrationFailedEvent(Exception ex);


}
