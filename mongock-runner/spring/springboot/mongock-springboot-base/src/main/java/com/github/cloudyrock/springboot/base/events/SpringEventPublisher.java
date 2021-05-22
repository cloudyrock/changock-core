package com.github.cloudyrock.springboot.base.events;

import com.github.cloudyrock.mongock.runner.core.event.EventPublisher;
import com.github.cloudyrock.mongock.runner.core.event.MigrationResult;
import com.github.cloudyrock.mongock.runner.core.event.MongockMigrationFailureEvent;
import com.github.cloudyrock.mongock.runner.core.event.MongockMigrationSuccessEvent;
import org.springframework.context.ApplicationEventPublisher;

import java.util.function.Consumer;

public class SpringEventPublisher implements EventPublisher {


  private final Runnable migrationStartedListener;
  private final Consumer<SpringMigrationSuccessEvent> migrationSuccessListener;
  private final Consumer<SpringMigrationFailureEvent> migrationFailedListener;

  public SpringEventPublisher(Runnable migrationStartedListener,
                              Consumer<SpringMigrationSuccessEvent> migrationSuccessListener,
                              Consumer<SpringMigrationFailureEvent> migrationFailedListener) {
    this.migrationSuccessListener = migrationSuccessListener;
    this.migrationFailedListener = migrationFailedListener;
    this.migrationStartedListener = migrationStartedListener;
  }

  @Override
  public void publishMigrationStarted() {
    if (migrationStartedListener != null) {
      migrationStartedListener.run();
    }
  }

  @Override
  public void publishMigrationSuccessEvent(MigrationResult migrationResult) {
    if (migrationSuccessListener != null) {
      migrationSuccessListener.accept(new SpringMigrationSuccessEvent(this, migrationResult));
    }
  }

  @Override
  public void publishMigrationFailedEvent(Exception ex) {
    if (migrationFailedListener != null) {
      migrationFailedListener.accept(new SpringMigrationFailureEvent(this, ex));
    }
  }


}
