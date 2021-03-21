package com.github.cloudyrock.springboot.v2_2;

import com.github.cloudyrock.mongock.config.LegacyMigration;
import com.github.cloudyrock.mongock.config.MongockSpringConfiguration;
import com.github.cloudyrock.mongock.driver.api.driver.ChangeSetDependency;
import com.github.cloudyrock.mongock.driver.api.driver.ConnectionDriver;
import com.github.cloudyrock.mongock.exception.MongockException;
import com.github.cloudyrock.mongock.runner.core.builder.RunnerBuilderBase;
import com.github.cloudyrock.mongock.runner.core.event.EventPublisher;
import com.github.cloudyrock.mongock.runner.core.executor.DefaultDependencyContext;
import com.github.cloudyrock.mongock.runner.core.executor.DependencyContext;
import com.github.cloudyrock.mongock.runner.core.executor.DependencyManager;
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
  protected DependencyManager dependencyManager;
  protected EventPublisher applicationEventPublisher =  new SpringEventPublisher(null);
  protected List<String> activeProfiles;

  /**
   * Set ApplicationContext from Spring
   *
   * @param springContext org.springframework.config.ApplicationContext object to inject
   * @return Mongock builder for fluent interface
   * @see org.springframework.context.annotation.Profile
   */
  public BUILDER_TYPE setSpringContext(ApplicationContext springContext) {
    DefaultDependencyContext dependencyContext =
        new DefaultDependencyContext(type -> springContext.getBean(type), name -> springContext.getBean(name));
    addDependencyManager(dependencyContext);
    setActiveProfiles(getActiveProfilesFromContext(springContext));
    return getInstance();
  }

  public BUILDER_TYPE setEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
    this.applicationEventPublisher = new SpringEventPublisher(applicationEventPublisher);
    return getInstance();
  }

  private List<String> getActiveProfilesFromContext(ApplicationContext springContext) {
    Environment springEnvironment = springContext.getEnvironment();
    return springEnvironment != null && CollectionUtils.isNotNullOrEmpty(springEnvironment.getActiveProfiles())
        ? Arrays.asList(springEnvironment.getActiveProfiles())
        : Collections.singletonList(DEFAULT_PROFILE);
  }


  ///generic

  private BUILDER_TYPE setActiveProfiles(List<String> activeProfiles) {
    this.activeProfiles = activeProfiles;
    return getInstance();
  }

  private BUILDER_TYPE addDependencyManager(DependencyContext dependencyContext) {
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
  public BUILDER_TYPE setConfig(SPRING_CONFIG config) {
    super.setConfig(config);
    return getInstance();
  }

  public abstract SPRING_APP_RUNNER_TYPE buildApplicationRunner();

  public abstract SPRING_INIT_BEAN_TYPE buildInitializingBeanRunner();

  //Following methods are used to build the runners. All of them are protected in case they need to be overwritten by
  //children classes

  protected SpringMigrationExecutor buildExecutorWithEnvironmentDependency() {
    checkDependencyManagerNotNull();
    return new SpringMigrationExecutor(
        driver,
        dependencyManager,
        new MigrationExecutorConfiguration(trackIgnored),
        metadata
    );
  }


  @Override
  protected Function<AnnotatedElement, Boolean> getAnnotationFilter() {
    return annotated -> ProfileUtil.matchesActiveSpringProfile(
            activeProfiles,
            Profile.class,
            annotated,
            (AnnotatedElement element) ->element.getAnnotation(Profile.class).value());
  }



  @Override
  public void runValidation() {
    super.runValidation();
    checkDependencyManagerNotNull();
  }


  private void checkDependencyManagerNotNull() {
    if (dependencyManager == null) {
      throw new MongockException("ApplicationContext from Spring must be injected to Builder");
    }
  }
}


