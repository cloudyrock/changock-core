package com.github.cloudyrock.standalone;

import com.github.cloudyrock.mongock.ChangeLogItemBase;
import com.github.cloudyrock.mongock.config.MongockConfiguration;
import com.github.cloudyrock.mongock.runner.core.builder.RunnerBuilderBase;
import com.github.cloudyrock.mongock.runner.core.event.EventPublisher;
import com.github.cloudyrock.mongock.runner.core.event.MigrationFailureEvent;
import com.github.cloudyrock.mongock.runner.core.event.MigrationStartedEvent;
import com.github.cloudyrock.mongock.runner.core.event.MigrationSuccessEvent;
import com.github.cloudyrock.mongock.runner.core.executor.ExecutorFactory;
import com.github.cloudyrock.mongock.runner.core.executor.dependency.DependencyManager;
import com.github.cloudyrock.mongock.runner.core.executor.operation.Operation;

import java.util.function.Consumer;

public abstract class StandaloneBuilderBase<SELF extends StandaloneBuilderBase<SELF, R, CHANGELOG, CONFIG>, R, CHANGELOG extends ChangeLogItemBase, CONFIG extends MongockConfiguration>
    extends RunnerBuilderBase<SELF, R, CHANGELOG, CONFIG> {

  protected StandaloneBuilderBase(Operation<R> operation, ExecutorFactory<CHANGELOG, CONFIG, R> executorFactory, CONFIG config) {
    super(operation, executorFactory, config, new DependencyManager());
  }

  public SELF setMigrationStartedListener(Consumer<MigrationStartedEvent> listener) {
    this.eventPublisher = new EventPublisher(
        () -> listener.accept(new MigrationStartedEvent()),
        eventPublisher.getMigrationSuccessListener(),
        eventPublisher.getMigrationFailedListener());
    return getInstance();
  }

  public SELF setMigrationSuccessListener(Consumer<MigrationSuccessEvent> listener) {
    this.eventPublisher = new EventPublisher(
        eventPublisher.getMigrationStartedListener(),
        result -> listener.accept(new MigrationSuccessEvent(result)),
        eventPublisher.getMigrationFailedListener());
    return getInstance();
  }

  public SELF setMigrationFailureListener(Consumer<MigrationFailureEvent> listener) {
    this.eventPublisher = new EventPublisher(
        eventPublisher.getMigrationStartedListener(),
        eventPublisher.getMigrationSuccessListener(),
        result -> listener.accept(new MigrationFailureEvent(result)));
    return getInstance();
  }


}
