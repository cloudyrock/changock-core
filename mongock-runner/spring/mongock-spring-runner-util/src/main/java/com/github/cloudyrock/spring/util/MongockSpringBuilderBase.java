package com.github.cloudyrock.spring.util;

import com.github.cloudyrock.mongock.config.LegacyMigration;
import com.github.cloudyrock.mongock.config.MongockSpringConfiguration;
import com.github.cloudyrock.mongock.driver.api.driver.ChangeSetDependency;
import com.github.cloudyrock.mongock.driver.api.driver.ConnectionDriver;
import com.github.cloudyrock.mongock.exception.MongockException;
import com.github.cloudyrock.mongock.runner.core.builder.RunnerBuilderBase;
import com.github.cloudyrock.mongock.runner.core.event.EventPublisher;
import com.github.cloudyrock.mongock.runner.core.executor.DependencyContext;
import com.github.cloudyrock.mongock.runner.core.executor.DependencyManager;
import com.github.cloudyrock.mongock.runner.core.executor.DependencyManagerWithContext;

import static com.github.cloudyrock.mongock.config.MongockConstants.LEGACY_MIGRATION_NAME;

public abstract class MongockSpringBuilderBase<BUILDER_TYPE extends MongockSpringBuilderBase>
    extends RunnerBuilderBase<BUILDER_TYPE, ConnectionDriver, MongockSpringConfiguration> {

  protected DependencyManager dependencyManager;
  protected EventPublisher applicationEventPublisher = EventPublisher.empty();

  protected BUILDER_TYPE addDependencyManager(DependencyContext dependencyContext) {
    this.dependencyManager = new DependencyManagerWithContext(dependencyContext);
    if (legacyMigration != null) {
      dependencyManager.addStandardDependency(
          new ChangeSetDependency(LEGACY_MIGRATION_NAME, LegacyMigration.class, legacyMigration)
      );
    }
    this.dependencyManager.addDriverDependencies(dependencies);
    return getInstance();
  }

  protected BUILDER_TYPE setEventPublisher(EventPublisher eventPublisher) {
    this.applicationEventPublisher = eventPublisher;
    return getInstance();
  }


  @Override
  public BUILDER_TYPE setConfig(MongockSpringConfiguration config) {
    super.setConfig(config);
    return getInstance();
  }

  @Override
  public void runValidation() {
    super.runValidation();
    if (dependencyManager == null) {
      throw new MongockException("ApplicationContext from Spring must be injected to Builder");
    }
  }


}


