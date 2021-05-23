package com.github.cloudyrock.standalone;

import com.github.cloudyrock.mongock.config.MongockConfiguration;
import com.github.cloudyrock.mongock.runner.core.builder.RunnerBuilderBase;
import com.github.cloudyrock.mongock.runner.core.event.result.MigrationResult;
import com.github.cloudyrock.mongock.runner.core.executor.ExecutorFactory;
import com.github.cloudyrock.mongock.runner.core.executor.dependency.DependencyManager;
import com.github.cloudyrock.mongock.runner.core.executor.operation.Operation;
import com.github.cloudyrock.mongock.runner.core.event.MongockEventPublisher;

import java.util.function.Consumer;

public abstract class StandaloneBuilderBase<BUILDER_TYPE extends StandaloneBuilderBase, RETURN_TYPE, CONFIG extends MongockConfiguration>
    extends RunnerBuilderBase<BUILDER_TYPE, RETURN_TYPE, CONFIG> {


  protected StandaloneBuilderBase(Operation<RETURN_TYPE> operation, ExecutorFactory<CONFIG> executorFactory, CONFIG config) {
    super(operation, executorFactory, config);
    dependencyManager = new DependencyManager();
  }

  //TODO javadoc
  public BUILDER_TYPE setMigrationStartedListener(Runnable migrationStartedListener) {
    this.eventPublisher = new MongockEventPublisher(migrationStartedListener,
        ((MongockEventPublisher)eventPublisher).getMigrationSuccessListener(),
        ((MongockEventPublisher)eventPublisher).getMigrationFailedListener());
    return getInstance();
  }

  //TODO javadoc
  public BUILDER_TYPE setMigrationSuccessListener(Consumer<MigrationResult> listener) {

    this.eventPublisher = new MongockEventPublisher(
        ((MongockEventPublisher)eventPublisher).getMigrationStartedListener(),
        listener,
        ((MongockEventPublisher)eventPublisher).getMigrationFailedListener());
    return getInstance();
  }

  //TODO javadoc
  public BUILDER_TYPE setMigrationFailureListener(Consumer<Exception> migrationFailureListener) {

    this.eventPublisher = new MongockEventPublisher(
        ((MongockEventPublisher)eventPublisher).getMigrationStartedListener(),
        ((MongockEventPublisher)eventPublisher).getMigrationSuccessListener(),
        migrationFailureListener);
    return getInstance();
  }

  ///////////////////////////////////////////////////
  // Build METHODS
  ///////////////////////////////////////////////////



}
