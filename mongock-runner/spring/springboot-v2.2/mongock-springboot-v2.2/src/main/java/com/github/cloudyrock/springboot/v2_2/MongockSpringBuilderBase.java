package com.github.cloudyrock.springboot.v2_2;

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

import java.util.List;

import static com.github.cloudyrock.mongock.config.MongockConstants.LEGACY_MIGRATION_NAME;

public abstract class MongockSpringBuilderBase<BUILDER_TYPE extends MongockSpringBuilderBase>
    extends RunnerBuilderBase<BUILDER_TYPE, ConnectionDriver, MongockSpringConfiguration> {

  static final String DEFAULT_PROFILE = "default";
  DependencyManager dependencyManager;
  EventPublisher applicationEventPublisher = EventPublisher.empty();
  List<String> activeProfiles;

  protected BUILDER_TYPE setActiveProfiles(List<String> activeProfiles) {
    this.activeProfiles = activeProfiles;
    return getInstance();
  }

  protected BUILDER_TYPE addDependencyManager(DependencyContext dependencyContext) {
    this.dependencyManager = new DependencyManagerWithContext(dependencyContext);
    addLegacyMigration();
    this.dependencyManager.addDriverDependencies(dependencies);
    return getInstance();
  }

  private void addLegacyMigration() {
    if (legacyMigration != null) {
      dependencyManager.addStandardDependency(
          new ChangeSetDependency(LEGACY_MIGRATION_NAME, LegacyMigration.class, legacyMigration)
      );
    }
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


