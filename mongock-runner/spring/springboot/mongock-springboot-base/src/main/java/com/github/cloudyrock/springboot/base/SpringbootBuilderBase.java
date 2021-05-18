package com.github.cloudyrock.springboot.base;

import com.github.cloudyrock.mongock.config.LegacyMigration;
import com.github.cloudyrock.mongock.config.executor.ExecutorConfiguration;
import com.github.cloudyrock.mongock.driver.api.driver.ChangeSetDependency;
import com.github.cloudyrock.mongock.exception.MongockException;
import com.github.cloudyrock.mongock.runner.core.builder.RunnerBuilderBase;
import com.github.cloudyrock.mongock.runner.core.event.EventPublisher;
import com.github.cloudyrock.mongock.runner.core.executor.ExecutorFactory;
import com.github.cloudyrock.mongock.runner.core.executor.MongockRunner;
import com.github.cloudyrock.mongock.runner.core.executor.dependency.DependencyManager;
import com.github.cloudyrock.mongock.runner.core.executor.dependency.DependencyManagerWithContext;
import com.github.cloudyrock.mongock.utils.CollectionUtils;
import com.github.cloudyrock.spring.config.MongockSpringConfigurationBase;
import com.github.cloudyrock.spring.util.ProfileUtil;
import com.github.cloudyrock.springboot.base.context.SpringDependencyContext;
import com.github.cloudyrock.springboot.base.events.SpringEventPublisher;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

import javax.inject.Named;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static com.github.cloudyrock.mongock.config.MongockConstants.LEGACY_MIGRATION_NAME;

public abstract class SpringbootBuilderBase<BUILDER_TYPE extends SpringbootBuilderBase, CONFIG extends MongockSpringConfigurationBase, EXECUTOR_CONFIG extends ExecutorConfiguration>
    extends RunnerBuilderBase<BUILDER_TYPE, CONFIG, EXECUTOR_CONFIG> {

  private ApplicationContext springContext;
  private List<String> activeProfiles;
  protected MongockRunner runner;
  private DependencyManager dependencyManager;
  private EventPublisher applicationEventPublisher = EventPublisher.empty();

  private static final String DEFAULT_PROFILE = "default";

  protected SpringbootBuilderBase(ExecutorFactory<EXECUTOR_CONFIG> executorFactory, CONFIG config) {
    super(executorFactory, config);
  }

  //TODO javadoc
  public BUILDER_TYPE setSpringContext(ApplicationContext springContext) {
    this.springContext = springContext;
    this.dependencyManager = new DependencyManagerWithContext(new SpringDependencyContext(springContext));
    return getInstance();
  }

  //TODO javadoc
  public BUILDER_TYPE setEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
    this.applicationEventPublisher = new SpringEventPublisher(applicationEventPublisher);
    return getInstance();
  }

  //TODO javadoc
  public abstract <T extends MongockApplicationRunnerBase> T buildApplicationRunner();

  //TODO javadoc
  public abstract <T extends MongockInitializingBeanRunnerBase> T buildInitializingBeanRunner();

  ///////////////////////////////////////////////////
  // Build methods
  ///////////////////////////////////////////////////
  @Override
  protected Function<AnnotatedElement, Boolean> getAnnotationFilter() {
    return annotated -> ProfileUtil.matchesActiveSpringProfile(
        activeProfiles,
        Profile.class,
        annotated,
        (AnnotatedElement element) -> element.getAnnotation(Profile.class).value());
  }

  @Override
  protected void beforeBuildRunner() {
    setActiveProfilesFromContext(springContext);
    if (config.getLegacyMigration() != null) {
      dependencyManager.addStandardDependency(
          new ChangeSetDependency(LEGACY_MIGRATION_NAME, LegacyMigration.class, config.getLegacyMigration())
      );
    }
    this.dependencyManager.addDriverDependencies(dependencies);
  }

  @Override
  protected Function<Parameter, String> buildParameterNameFunction() {
    return parameter -> {
      String name = parameter.isAnnotationPresent(Named.class) ? parameter.getAnnotation(Named.class).value() : null;
      if (name == null) {
        name = parameter.isAnnotationPresent(Qualifier.class) ? parameter.getAnnotation(Qualifier.class).value() : null;
      }
      return name;
    };
  }

  @Override
  protected DependencyManager buildDependencyManager() {
    return dependencyManager;
  }

  @Override
  protected EventPublisher buildEventPublisher() {
    return applicationEventPublisher;
  }

  @FunctionalInterface
  public interface MongockApplicationRunnerBase extends ApplicationRunner {
  }

  @FunctionalInterface
  public interface MongockInitializingBeanRunnerBase extends InitializingBean {
  }

  @Override
  public void runValidation() {
    super.runValidation();
    if (dependencyManager == null) {
      throw new MongockException("ApplicationContext from Spring must be injected to Builder");
    }
  }

  private void setActiveProfilesFromContext(ApplicationContext springContext) {
    Environment springEnvironment = springContext.getEnvironment();
    this.activeProfiles = springEnvironment != null && CollectionUtils.isNotNullOrEmpty(springEnvironment.getActiveProfiles())
        ? Arrays.asList(springEnvironment.getActiveProfiles())
        : Collections.singletonList(DEFAULT_PROFILE);
  }
}
