package com.github.cloudyrock.springboot.v2_2;

import com.github.cloudyrock.mongock.config.LegacyMigration;
import com.github.cloudyrock.mongock.config.MongockSpringConfiguration;
import com.github.cloudyrock.mongock.driver.api.driver.ChangeSetDependency;
import com.github.cloudyrock.mongock.driver.api.driver.ConnectionDriver;
import com.github.cloudyrock.mongock.exception.MongockException;
import com.github.cloudyrock.mongock.runner.core.builder.RunnerBuilderBase;
import com.github.cloudyrock.mongock.runner.core.executor.DefaultDependencyContext;
import com.github.cloudyrock.mongock.runner.core.executor.DependencyManagerWithContext;
import com.github.cloudyrock.mongock.runner.core.executor.MigrationExecutorConfiguration;
import com.github.cloudyrock.mongock.utils.CollectionUtils;
import com.github.cloudyrock.spring.util.ProfileUtil;
import com.github.cloudyrock.springboot.v2_2.events.SpringEventPublisher;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static com.github.cloudyrock.mongock.config.MongockConstants.LEGACY_MIGRATION_NAME;

public abstract class MongockSpringBuilderBase<
    BUILDER_TYPE extends MongockSpringBuilderBase,
    SPRING_APP_RUNNER_TYPE extends ApplicationRunner,
    SPRING_INIT_BEAN_TYPE extends InitializingBean,
    DRIVER extends ConnectionDriver,
    SPRING_CONFIG extends MongockSpringConfiguration>
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

  public abstract SPRING_APP_RUNNER_TYPE buildApplicationRunner();

  public abstract SPRING_INIT_BEAN_TYPE buildInitializingBeanRunner();

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
    DependencyManagerWithContext dependencyManager = new DependencyManagerWithContext(
      new DefaultDependencyContext(type -> springContext.getBean(type), name -> springContext.getBean(name)
      )
    );
    if (legacyMigration != null) {
      dependencyManager.addStandardDependency(
          new ChangeSetDependency(LEGACY_MIGRATION_NAME, LegacyMigration.class, legacyMigration)
      );
    }
    dependencyManager.addDriverDependencies(dependencies);
    return dependencyManager;
  }

  @Override
  protected Function<AnnotatedElement, Boolean> getAnnotationFilter() {
    if (springContext == null) {
      throw new MongockException("ApplicationContext from Spring must be injected to Builder");
    }
    return annotated -> ProfileUtil.matchesActiveSpringProfile(
            getActiveProfiles(),
            Profile.class,
            annotated,
            (AnnotatedElement element) ->element.getAnnotation(Profile.class).value());
  }

  private List<String> getActiveProfiles() {
    Environment springEnvironment = springContext.getEnvironment();
    return springEnvironment != null && CollectionUtils.isNotNullOrEmpty(springEnvironment.getActiveProfiles())
        ? Arrays.asList(springEnvironment.getActiveProfiles())
        : Collections.singletonList(DEFAULT_PROFILE);
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


