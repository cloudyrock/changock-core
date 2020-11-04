package io.changock.runner.spring.v5;

import io.changock.driver.api.driver.ChangeSetDependency;
import io.changock.driver.api.driver.ConnectionDriver;
import io.changock.migration.api.exception.ChangockException;
import io.changock.runner.core.ChangockBase;
import io.changock.runner.core.MigrationExecutorConfiguration;
import io.changock.runner.core.builder.RunnerBuilderBase;
import io.changock.runner.core.builder.configuration.LegacyMigration;
import io.changock.runner.spring.util.SpringDependencyContext;
import io.changock.runner.core.DependencyManagerWithContext;
import io.changock.runner.spring.util.SpringEventPublisher;
import io.changock.runner.spring.util.config.ChangockSpringConfiguration;
import io.changock.runner.spring.v5.core.ProfiledChangeLogService;
import io.changock.runner.spring.v5.core.SpringMigrationExecutor;
import io.changock.utils.CollectionUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static io.changock.runner.core.builder.configuration.ChangockConstants.LEGACY_MIGRATION_NAME;

public abstract class ChangockSpringBuilderBase<BUILDER_TYPE extends ChangockSpringBuilderBase, DRIVER extends ConnectionDriver, SPRING_CONFIG extends ChangockSpringConfiguration>
    extends RunnerBuilderBase<BUILDER_TYPE, DRIVER, SPRING_CONFIG> {

  protected static final String DEFAULT_PROFILE = "default";
  protected ApplicationContext springContext;
  protected ApplicationEventPublisher applicationEventPublisher;

  /**
   * Set ApplicationContext from Spring
   *
   * @param springContext org.springframework.context.ApplicationContext object to inject
   * @return Changock builder for fluent interface
   * @see org.springframework.context.annotation.Profile
   */
  public BUILDER_TYPE setSpringContext(ApplicationContext springContext) {
    this.springContext = springContext;
    return returnInstance();
  }

  public BUILDER_TYPE setSpringEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
    this.applicationEventPublisher = applicationEventPublisher;
    return returnInstance();
  }

  @Override
  public BUILDER_TYPE setConfig(SPRING_CONFIG config) {
    super.setConfig(config);
    return returnInstance();
  }


  public SpringApplicationRunner buildApplicationRunner() {
    return new SpringApplicationRunner(
        buildExecutorWithEnvironmentDependency(),
        buildProfiledChangeLogService(),
        throwExceptionIfCannotObtainLock,
        enabled,
        buildSpringEventPublisher());
  }

  public SpringInitializingBean buildInitializingBeanRunner() {
    return new SpringInitializingBean(
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
      throw new ChangockException("ApplicationContext from Spring must be injected to Builder");
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
      throw new ChangockException("ApplicationContext from Spring must be injected to Builder");
    }
  }

  public static class SpringApplicationRunner extends ChangockBase implements ApplicationRunner {

    protected SpringApplicationRunner(SpringMigrationExecutor executor,
                                      ProfiledChangeLogService changeLogService,
                                      boolean throwExceptionIfCannotObtainLock,
                                      boolean enabled,
                                      SpringEventPublisher eventPublisher) {
      super(executor, changeLogService, throwExceptionIfCannotObtainLock, enabled, eventPublisher);
    }

    @Override
    public void run(ApplicationArguments args) {
      this.execute();
    }
  }

  public static class SpringInitializingBean extends ChangockBase implements InitializingBean {

    protected SpringInitializingBean(SpringMigrationExecutor executor,
                                     ProfiledChangeLogService changeLogService,
                                     boolean throwExceptionIfCannotObtainLock,
                                     boolean enabled,
                                     SpringEventPublisher eventPublisher) {
      super(executor, changeLogService, throwExceptionIfCannotObtainLock, enabled, eventPublisher);
    }

    @Override
    public void afterPropertiesSet() {
      execute();
    }
  }
}


