package com.github.cloudyrock.springboot.base.builder;

import com.github.cloudyrock.mongock.config.MongockConfiguration;
import com.github.cloudyrock.mongock.driver.api.driver.ChangeSetDependency;
import com.github.cloudyrock.mongock.exception.MongockException;
import com.github.cloudyrock.mongock.runner.core.builder.RunnerBuilderBase;
import com.github.cloudyrock.mongock.runner.core.event.EventPublisher;
import com.github.cloudyrock.mongock.runner.core.executor.ExecutorFactory;
import com.github.cloudyrock.mongock.runner.core.executor.dependency.DependencyContext;
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

public abstract class SpringbootBuilderBase<SELF extends SpringbootBuilderBase<SELF, R, CONFIG>, R, CONFIG extends MongockConfiguration>
    extends RunnerBuilderBase<SELF, R, CONFIG> {

  private static final String DEFAULT_PROFILE = "default";

  protected SpringbootBuilderBase(Operation<R> operation, ExecutorFactory<CONFIG, R> executorFactory, CONFIG config) {
    super(operation, executorFactory, config, new DependencyManagerWithContext());
    parameterNameFunction = buildParameterNameFunctionForSpring();
  }

  public SELF setSpringContext(ApplicationContext springContext) {
    (getDependencyManager()).setContext(new SpringDependencyContext(springContext));
    return getInstance();
  }

  public SELF setEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
    if (applicationEventPublisher == null) {
      throw new MongockException("EventPublisher cannot e null");
    }
    this.eventPublisher = new EventPublisher(
        () -> applicationEventPublisher.publishEvent(new SpringMigrationStartedEvent(this)),
        result -> applicationEventPublisher.publishEvent(new SpringMigrationSuccessEvent(this, result)),
        result -> applicationEventPublisher.publishEvent(new SpringMigrationFailureEvent(this, result))
    );
    return getInstance();
  }


  ///////////////////////////////////////////////////
  // Build methods
  ///////////////////////////////////////////////////


  public MongockApplicationRunner<R> buildApplicationRunner() {
    return new MongockApplicationRunner<>(buildRunner());
  }


  public MongockInitializingBeanRunner<R> buildInitializingBeanRunner() {
    return new MongockInitializingBeanRunner<>(buildRunner());
  }

  @Override
  protected Function<AnnotatedElement, Boolean> getAnnotationFilter() {
    DependencyContext dependencyContext = getDependencyManager().getDependencyContext();
    ApplicationContext springContext = ((SpringDependencyContext)dependencyContext).getSpringContext();
    return annotated -> ProfileUtil.matchesActiveSpringProfile(
        getActiveProfilesFromContext(springContext),
        Profile.class,
        annotated,
        (AnnotatedElement element) -> element.getAnnotation(Profile.class).value());
  }

  @Override
  public void runValidation() {
    super.runValidation();
    if (!(getDependencyManager()).isContextPresent()) {
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

  public DependencyManagerWithContext getDependencyManager() {
    return (DependencyManagerWithContext) dependencyManager;
  }

}
