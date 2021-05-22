package com.github.cloudyrock.standalone;

import com.github.cloudyrock.mongock.config.MongockConfiguration;
import com.github.cloudyrock.mongock.runner.core.builder.RunnerBuilderBase;
import com.github.cloudyrock.mongock.runner.core.event.EventPublisher;
import com.github.cloudyrock.mongock.runner.core.executor.ExecutorFactory;
import com.github.cloudyrock.mongock.runner.core.executor.MongockRunner;
import com.github.cloudyrock.mongock.runner.core.executor.dependency.DependencyManager;
import com.github.cloudyrock.mongock.runner.core.executor.operation.Operation;
import com.github.cloudyrock.mongock.runner.core.executor.operation.change.MigrationOp;
import com.github.cloudyrock.standalone.event.StandaloneEventPublisher;
import com.github.cloudyrock.standalone.event.StandaloneMigrationFailureEvent;
import com.github.cloudyrock.standalone.event.StandaloneMigrationSuccessEvent;

import java.util.function.Consumer;

public abstract class StandaloneBuilderBase<BUILDER_TYPE extends StandaloneBuilderBase, RETURN_TYPE, CONFIG extends MongockConfiguration>
    extends RunnerBuilderBase<BUILDER_TYPE, RETURN_TYPE, CONFIG> {


  protected StandaloneBuilderBase(Operation<RETURN_TYPE> operation, ExecutorFactory<CONFIG> executorFactory, CONFIG config) {
    super(operation, executorFactory, config);
    dependencyManager = new DependencyManager();
  }

  //TODO javadoc
  public BUILDER_TYPE setMigrationStartedListener(Runnable migrationStartedListener) {
    this.eventPublisher = new StandaloneEventPublisher(migrationStartedListener,
        ((StandaloneEventPublisher)eventPublisher).getMigrationSuccessListener(),
        ((StandaloneEventPublisher)eventPublisher).getMigrationFailedListener());
    return getInstance();
  }

  //TODO javadoc
  public BUILDER_TYPE setMigrationSuccessListener(Consumer<StandaloneMigrationSuccessEvent> listener) {

    this.eventPublisher = new StandaloneEventPublisher(
        ((StandaloneEventPublisher)eventPublisher).getMigrationStartedListener(),
        listener,
        ((StandaloneEventPublisher)eventPublisher).getMigrationFailedListener());
    return getInstance();
  }

  //TODO javadoc
  public BUILDER_TYPE setMigrationFailureListener(Consumer<StandaloneMigrationFailureEvent> migrationFailureListener) {

    this.eventPublisher = new StandaloneEventPublisher(
        ((StandaloneEventPublisher)eventPublisher).getMigrationStartedListener(),
        ((StandaloneEventPublisher)eventPublisher).getMigrationSuccessListener(),
        migrationFailureListener);
    return getInstance();
  }

  ///////////////////////////////////////////////////
  // Build METHODS
  ///////////////////////////////////////////////////



}
