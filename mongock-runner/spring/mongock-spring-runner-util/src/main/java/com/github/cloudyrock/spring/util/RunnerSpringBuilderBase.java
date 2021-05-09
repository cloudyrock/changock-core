package com.github.cloudyrock.spring.util;

import com.github.cloudyrock.mongock.config.LegacyMigration;
import com.github.cloudyrock.mongock.config.MongockSpringConfiguration;
import com.github.cloudyrock.mongock.driver.api.driver.ChangeSetDependency;
import com.github.cloudyrock.mongock.exception.MongockException;
import com.github.cloudyrock.mongock.runner.core.builder.RunnerBuilderBase;
import com.github.cloudyrock.mongock.runner.core.event.EventPublisher;
import com.github.cloudyrock.mongock.runner.core.executor.DependencyContext;
import com.github.cloudyrock.mongock.runner.core.executor.DependencyManager;
import com.github.cloudyrock.mongock.runner.core.executor.DependencyManagerWithContext;
import com.github.cloudyrock.mongock.runner.core.executor.MigrationExecutor;
import com.github.cloudyrock.mongock.runner.core.executor.MigrationExecutorConfiguration;
import com.github.cloudyrock.mongock.runner.core.executor.MigrationExecutorImpl;

import java.lang.reflect.Parameter;
import java.util.function.Function;

import static com.github.cloudyrock.mongock.config.MongockConstants.LEGACY_MIGRATION_NAME;

public abstract class RunnerSpringBuilderBase<BUILDER_TYPE extends RunnerSpringBuilderBase>
    extends RunnerBuilderBase<BUILDER_TYPE, MongockSpringConfiguration> {

  private DependencyManager dependencyManager;
  protected EventPublisher applicationEventPublisher = EventPublisher.empty();

  protected BUILDER_TYPE addDependencyManager(DependencyContext dependencyContext) {
    this.dependencyManager = new DependencyManagerWithContext(dependencyContext);
    return getInstance();
  }

  protected BUILDER_TYPE setEventPublisher(EventPublisher eventPublisher) {
    this.applicationEventPublisher = eventPublisher;
    return getInstance();
  }


  protected void injectLegacyMigration() {
    if (legacyMigration != null) {
      dependencyManager.addStandardDependency(
          new ChangeSetDependency(LEGACY_MIGRATION_NAME, LegacyMigration.class, legacyMigration)
      );
    }
    this.dependencyManager.addDriverDependencies(dependencies);
  }

  @Override
  protected MigrationExecutor buildMigrationExecutor(Function<Parameter, String> paramNameExtractor) {
    return new MigrationExecutorImpl(driver, dependencyManager, new MigrationExecutorConfiguration(trackIgnored, serviceIdentifier), metadata, paramNameExtractor);
  }

  @Override
  public void runValidation() {
    super.runValidation();
    if (dependencyManager == null) {
      throw new MongockException("ApplicationContext from Spring must be injected to Builder");
    }
  }


}


