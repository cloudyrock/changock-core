package com.github.cloudyrock.springboot.base.builder;

import com.github.cloudyrock.mongock.config.LegacyMigration;
import com.github.cloudyrock.mongock.config.MongockConfiguration;
import com.github.cloudyrock.mongock.driver.api.driver.ChangeSetDependency;
import com.github.cloudyrock.mongock.exception.MongockException;
import com.github.cloudyrock.mongock.runner.core.builder.RunnerBuilderBase;
import com.github.cloudyrock.mongock.runner.core.event.MongockEventPublisher;
import com.github.cloudyrock.mongock.runner.core.executor.ExecutorFactory;
import com.github.cloudyrock.mongock.runner.core.executor.dependency.DependencyManagerWithContext;
import com.github.cloudyrock.mongock.runner.core.executor.operation.Operation;
import com.github.cloudyrock.mongock.utils.CollectionUtils;
import com.github.cloudyrock.spring.util.ProfileUtil;
import com.github.cloudyrock.springboot.base.MongockApplicationRunner;
import com.github.cloudyrock.springboot.base.MongockInitializingBeanRunner;
import com.github.cloudyrock.springboot.base.context.SpringDependencyContext;
import com.github.cloudyrock.springboot.base.events.SpringMigrationFailureEvent;
import com.github.cloudyrock.springboot.base.events.SpringMigrationStartedEvent;
import com.github.cloudyrock.springboot.base.events.SpringMigrationSuccessEvent;
import org.springframework.beans.factory.annotation.Qualifier;
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

public abstract class SpringbootBuilderBase<BUILDER_TYPE extends SpringbootBuilderBase<BUILDER_TYPE, RETURN_TYPE, CONFIG>, RETURN_TYPE, CONFIG extends MongockConfiguration>
    extends RunnerBuilderBase<BUILDER_TYPE, RETURN_TYPE, CONFIG> {


  private List<String> activeProfiles;
  private static final String DEFAULT_PROFILE = "default";

  protected SpringbootBuilderBase(Operation<RETURN_TYPE> operation, ExecutorFactory<CONFIG> executorFactory, CONFIG config) {
    super(operation, executorFactory, config);
    parameterNameFunction = buildParameterNameFunctionForSpring();
  }

  public BUILDER_TYPE setSpringContext(ApplicationContext springContext) {
    this.activeProfiles = getActiveProfilesFromContext(springContext);
    this.dependencyManager = new DependencyManagerWithContext(new SpringDependencyContext(springContext));
    return getInstance();
  }

  public BUILDER_TYPE setEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
    if(applicationEventPublisher == null) {
      throw new MongockException("EventPublisher cannot e null");
    }
    this.eventPublisher = new MongockEventPublisher(
        () -> applicationEventPublisher.publishEvent(new SpringMigrationStartedEvent(this)),
        result -> applicationEventPublisher.publishEvent(new SpringMigrationSuccessEvent(this, result)),
        result -> applicationEventPublisher.publishEvent(new SpringMigrationFailureEvent(this, result))
    );
    return getInstance();
  }

  @Override
  public BUILDER_TYPE addDependency(String name, Class type, Object instance) {
    dependencyManager.addDriverDependency(new ChangeSetDependency(name, type, instance));
    return getInstance();
  }


  ///////////////////////////////////////////////////
  // Build methods
  ///////////////////////////////////////////////////


  public MongockApplicationRunner<RETURN_TYPE> buildApplicationRunner() {
    return new MongockApplicationRunner<>(buildRunner());
  }



  public MongockInitializingBeanRunner<RETURN_TYPE> buildInitializingBeanRunner() {
    return new MongockInitializingBeanRunner<>(buildRunner());
  }

  @Override
  protected void beforeBuildRunner() {
    if (config.getLegacyMigration() != null) {
      dependencyManager.addStandardDependency(
          new ChangeSetDependency(LEGACY_MIGRATION_NAME, LegacyMigration.class, config.getLegacyMigration())
      );
    }
  }

  @Override
  protected Function<AnnotatedElement, Boolean> getAnnotationFilter() {
    return annotated -> ProfileUtil.matchesActiveSpringProfile(
        activeProfiles,
        Profile.class,
        annotated,
        (AnnotatedElement element) -> element.getAnnotation(Profile.class).value());
  }

  @Override
  public void runValidation() {
    super.runValidation();
    if (dependencyManager == null) {
      throw new MongockException("ApplicationContext from Spring must be injected to Builder");
    }
  }


  private static List<String> getActiveProfilesFromContext(ApplicationContext springContext) {
    Environment springEnvironment = springContext.getEnvironment();
    return springEnvironment != null && CollectionUtils.isNotNullOrEmpty(springEnvironment.getActiveProfiles())
        ? Arrays.asList(springEnvironment.getActiveProfiles())
        : Collections.singletonList(DEFAULT_PROFILE);
  }

  private static Function<Parameter, String> buildParameterNameFunctionForSpring() {
    return parameter -> {
      String name = parameter.isAnnotationPresent(Named.class) ? parameter.getAnnotation(Named.class).value() : null;
      if (name == null) {
        name = parameter.isAnnotationPresent(Qualifier.class) ? parameter.getAnnotation(Qualifier.class).value() : null;
      }
      return name;
    };
  }
}
