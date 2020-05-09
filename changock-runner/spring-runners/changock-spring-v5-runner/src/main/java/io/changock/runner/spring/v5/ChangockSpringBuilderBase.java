package io.changock.runner.spring.v5;

import io.changock.driver.api.driver.ConnectionDriver;
import io.changock.migration.api.exception.ChangockException;
import io.changock.runner.core.ChangockBase;
import io.changock.runner.core.builder.RunnerBuilderBase;
import io.changock.runner.spring.util.SpringDependencyManager;
import io.changock.runner.spring.v5.core.ProfiledChangeLogService;
import io.changock.runner.spring.v5.core.SpringMigrationExecutor;
import io.changock.utils.CollectionUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class ChangockSpringBuilderBase<BUILDER_TYPE extends ChangockSpringBuilderBase, DRIVER extends ConnectionDriver>
    extends RunnerBuilderBase<BUILDER_TYPE, DRIVER> {

  protected static final String DEFAULT_PROFILE = "default";
  protected ApplicationContext springContext;

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

  protected SpringMigrationExecutor buildExecutorWithEnvironmentDependency() {
    return new SpringMigrationExecutor(
        driver,
        new SpringDependencyManager(this.springContext),
        lockAcquiredForMinutes,
        maxTries,
        maxWaitingForLockMinutes,
        metadata
    );
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
        Collections.singletonList(changeLogsScanPackage),
        startSystemVersion,
        endSystemVersion,
        activeProfiles,
        annotationProcessor
    );
  }

  @Override
  public void runValidation() {
    super.runValidation();
    if (springContext == null) {
      throw new ChangockException("ApplicationContext from Spring must be injected to Builder");
    }
  }

  public static class ChangockSpringApplicationRunner extends ChangockBase implements ApplicationRunner {

    protected ChangockSpringApplicationRunner(SpringMigrationExecutor executor,
                                              ProfiledChangeLogService changeLogService,
                                              boolean throwExceptionIfCannotObtainLock,
                                              boolean enabled) {
      super(executor, changeLogService, throwExceptionIfCannotObtainLock, enabled);
    }

    @Override
    public void run(ApplicationArguments args) {
      this.execute();
    }
  }

  public static class ChangockSpringInitializingBeanRunner extends ChangockBase implements InitializingBean {

    protected ChangockSpringInitializingBeanRunner(SpringMigrationExecutor executor,
                                                   ProfiledChangeLogService changeLogService,
                                                   boolean throwExceptionIfCannotObtainLock,
                                                   boolean enabled) {
      super(executor, changeLogService, throwExceptionIfCannotObtainLock, enabled);
    }

    @Override
    public void afterPropertiesSet() {
      execute();
    }
  }
}


