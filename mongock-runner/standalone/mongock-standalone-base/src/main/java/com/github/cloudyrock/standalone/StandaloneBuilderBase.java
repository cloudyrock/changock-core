package com.github.cloudyrock.standalone;

import com.github.cloudyrock.mongock.ChangeLogItemBase;
import com.github.cloudyrock.mongock.config.MongockConfiguration;
import com.github.cloudyrock.mongock.driver.api.entry.ChangeEntry;
import com.github.cloudyrock.mongock.runner.core.builder.RunnerBuilderBase;
import com.github.cloudyrock.mongock.runner.core.event.EventPublisher;
import com.github.cloudyrock.mongock.runner.core.event.MigrationFailureEvent;
import com.github.cloudyrock.mongock.runner.core.event.MigrationStartedEvent;
import com.github.cloudyrock.mongock.runner.core.event.MigrationSuccessEvent;
import com.github.cloudyrock.mongock.runner.core.executor.ExecutorFactory;
import com.github.cloudyrock.mongock.runner.core.executor.changelog.ChangeLogServiceBase;
import com.github.cloudyrock.mongock.runner.core.executor.dependency.DependencyManager;
import com.github.cloudyrock.mongock.runner.core.executor.operation.Operation;

import java.util.function.Consumer;

public abstract class StandaloneBuilderBase<
    SELF extends StandaloneBuilderBase<SELF, R, CHANGELOG, CHANGE_ENTRY, CONFIG>,
    R,
    CHANGELOG extends ChangeLogItemBase,
    CHANGE_ENTRY extends ChangeEntry,
    CONFIG extends MongockConfiguration>
    extends RunnerBuilderBase<SELF, R, CHANGELOG, CHANGE_ENTRY, CONFIG> {

  protected StandaloneBuilderBase(Operation<R> operation,
                                  ExecutorFactory<CHANGELOG, CHANGE_ENTRY, CONFIG, R> executorFactory,
                                  ChangeLogServiceBase<CHANGELOG> changeLogService,
                                  CONFIG config) {
    super(operation, executorFactory, changeLogService,new DependencyManager(), config);
  }

  public SELF setMigrationStartedListener(Consumer<MigrationStartedEvent> listener) {
    this.eventPublisher = new EventPublisher<>(
        () -> listener.accept(new MigrationStartedEvent()),
        eventPublisher.getMigrationSuccessListener(),
        eventPublisher.getMigrationFailedListener());
    return getInstance();
  }

  public SELF setMigrationSuccessListener(Consumer<MigrationSuccessEvent<R>> listener) {
    this.eventPublisher = new EventPublisher<>(
        eventPublisher.getMigrationStartedListener(),
        result -> listener.accept(new MigrationSuccessEvent<>(result)),
        eventPublisher.getMigrationFailedListener());
    return getInstance();
  }

  public SELF setMigrationFailureListener(Consumer<MigrationFailureEvent> listener) {
    this.eventPublisher = new EventPublisher<>(
        eventPublisher.getMigrationStartedListener(),
        eventPublisher.getMigrationSuccessListener(),
        result -> listener.accept(new MigrationFailureEvent(result)));
    return getInstance();
  }


}
