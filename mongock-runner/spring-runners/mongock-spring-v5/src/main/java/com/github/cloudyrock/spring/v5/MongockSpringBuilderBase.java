package com.github.cloudyrock.spring.v5;

import com.github.cloudyrock.mongock.exception.MongockException;
import com.github.cloudyrock.mongock.driver.api.driver.ChangeSetDependency;
import com.github.cloudyrock.mongock.driver.api.driver.ConnectionDriver;
import com.github.cloudyrock.mongock.runner.core.executor.MigrationExecutorConfiguration;
import com.github.cloudyrock.mongock.runner.core.builder.RunnerBuilderBase;
import com.github.cloudyrock.mongock.config.LegacyMigration;
import com.github.cloudyrock.spring.util.SpringDependencyContext;
import com.github.cloudyrock.mongock.runner.core.executor.DependencyManagerWithContext;
import com.github.cloudyrock.spring.util.SpringEventPublisher;
import com.github.cloudyrock.mongock.config.MongockSpringConfiguration;
import com.github.cloudyrock.spring.v5.core.ProfiledChangeLogService;
import com.github.cloudyrock.spring.v5.core.SpringMigrationExecutor;
import com.github.cloudyrock.mongock.utils.CollectionUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.github.cloudyrock.mongock.config.MongockConstants.LEGACY_MIGRATION_NAME;

public abstract class MongockSpringBuilderBase<BUILDER_TYPE extends MongockSpringBuilderBase, DRIVER extends ConnectionDriver, SPRING_CONFIG extends MongockSpringConfiguration>
    extends RunnerBuilderBase<BUILDER_TYPE, DRIVER, SPRING_CONFIG> {

  protected static final String DEFAULT_PROFILE = "default";
  protected ApplicationContext springContext;
  protected ApplicationEventPublisher applicationEventPublisher;

  /**
   * Set ApplicationContext from Spring
   *
   * @param springContext org.springframework.config.ApplicationContext object to inject
   * @return Mongock builder for fluent interface
   * @see org.springframework.context.annotation.Profile
   */
  public BUILDER_TYPE setSpringContext(ApplicationContext springContext) {
    this.springContext = springContext;
    return returnInstance();
  }

  public BUILDER_TYPE setEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
    this.applicationEventPublisher = applicationEventPublisher;
    return returnInstance();
  }

  @Override
  public BUILDER_TYPE setConfig(SPRING_CONFIG config) {
    super.setConfig(config);
    return returnInstance();
  }


  public MongockApplicationRunner buildApplicationRunner() {
    return new MongockApplicationRunner(
        buildExecutorWithEnvironmentDependency(),
        buildProfiledChangeLogService(),
        throwExceptionIfCannotObtainLock,
        enabled,
        buildSpringEventPublisher());
  }

  public MongockInitializingBeanRunner buildInitializingBeanRunner() {
    return new MongockInitializingBeanRunner(
        buildExecutorWithEnvironmentDependency(),
        buildProfiledChangeLogService(),
        throwExceptionIfCannotObtainLock,
        enabled,
        buildSpringEventPublisher());
  }


  //Following methods are used to build the runners. All of them are protected in case they need to be overwritten by
  //children classes

  protected SpringMigrationExecutor buildExecutorWithEnvironmentDependency() {
    return new SpringMigrationExecutor(
        driver,
        buildDependencyManagerWithContext(),
        new MigrationExecutorConfiguration(trackIgnored),
        metadata
    );
  }

  protected DependencyManagerWithContext buildDependencyManagerWithContext() {
    DependencyManagerWithContext dependencyManager = new DependencyManagerWithContext(new SpringDependencyContext(springContext));
    if (legacyMigration != null) {
      dependencyManager.addStandardDependency(
          new ChangeSetDependency(LEGACY_MIGRATION_NAME, LegacyMigration.class, legacyMigration)
      );
    }
    dependencyManager.addDriverDependencies(dependencies);
    return dependencyManager;
  }

  protected ProfiledChangeLogService buildProfiledChangeLogService() {
    if (springContext == null) {
      throw new MongockException("ApplicationContext from Spring must be injected to Builder");
    }
    Environment springEnvironment = springContext.getEnvironment();
    List<String> activeProfiles = springEnvironment != null && CollectionUtils.isNotNullOrEmpty(springEnvironment.getActiveProfiles())
        ? Arrays.asList(springEnvironment.getActiveProfiles())
        : Collections.singletonList(DEFAULT_PROFILE);
    return new ProfiledChangeLogService(
        changeLogsScanPackage,
        changeLogsScanClasses,
        startSystemVersion,
        endSystemVersion,
        activeProfiles,
        annotationProcessor
    );
  }

  protected SpringEventPublisher buildSpringEventPublisher() {
    return new SpringEventPublisher(applicationEventPublisher);
  }

  @Override
  public void runValidation() {
    super.runValidation();
    if (springContext == null) {
      throw new MongockException("ApplicationContext from Spring must be injected to Builder");
    }
  }

}


