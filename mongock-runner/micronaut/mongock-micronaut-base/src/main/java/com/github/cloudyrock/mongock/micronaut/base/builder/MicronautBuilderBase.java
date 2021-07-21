package com.github.cloudyrock.mongock.micronaut.base.builder;

import com.github.cloudyrock.mongock.ChangeLogItem;
import com.github.cloudyrock.mongock.ChangeSetItem;
import com.github.cloudyrock.mongock.config.MongockConfiguration;
import com.github.cloudyrock.mongock.driver.api.driver.ConnectionDriver;
import com.github.cloudyrock.mongock.driver.api.entry.ChangeEntry;
import com.github.cloudyrock.mongock.exception.MongockException;
import com.github.cloudyrock.mongock.micronaut.base.MongockApplicationRunner;
import com.github.cloudyrock.mongock.micronaut.base.context.MicronautDependencyContext;
import com.github.cloudyrock.mongock.micronaut.base.events.*;
import com.github.cloudyrock.mongock.runner.core.builder.RunnerBuilderBase;
import com.github.cloudyrock.mongock.runner.core.event.EventPublisher;
import com.github.cloudyrock.mongock.runner.core.executor.ExecutorFactory;
import com.github.cloudyrock.mongock.runner.core.executor.changelog.ChangeLogServiceBase;
import com.github.cloudyrock.mongock.runner.core.executor.dependency.DependencyManagerWithContext;
import com.github.cloudyrock.mongock.runner.core.executor.operation.Operation;
import com.github.cloudyrock.mongock.utils.CollectionUtils;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.env.Environment;
import io.micronaut.context.event.ApplicationEventPublisher;

import javax.inject.Named;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public abstract class MicronautBuilderBase<
    SELF extends MicronautBuilderBase<SELF, R, CHANGELOG, CHANGESET, CHANGE_ENTRY, CONFIG>,
    R,
    CHANGELOG extends ChangeLogItem<CHANGESET>,
    CHANGESET extends ChangeSetItem,
    CHANGE_ENTRY extends ChangeEntry,
    CONFIG extends MongockConfiguration>
    extends RunnerBuilderBase<SELF, R, CHANGELOG, CHANGESET, CHANGE_ENTRY, CONFIG> {

  private static final String DEFAULT_PROFILE = "default";

  protected MicronautBuilderBase(Operation<R> operation,
                                 ExecutorFactory<CHANGELOG, CHANGESET, CHANGE_ENTRY, CONFIG, R> executorFactory,
                                 ChangeLogServiceBase<CHANGELOG, CHANGESET> changeLogService,
                                 CONFIG config) {
    super(operation, executorFactory, changeLogService, new DependencyManagerWithContext(), config);
    parameterNameFunction = buildParameterNameFunctionForMicronaut();
  }


  private static List<String> getActiveProfilesFromContext(ApplicationContext micronautContext) {
    Environment environment = micronautContext.getEnvironment();
    return CollectionUtils.isNotNullOrEmpty(environment.getActiveNames())
        ? new ArrayList<>(environment.getActiveNames())
        : Collections.singletonList(DEFAULT_PROFILE);
  }

  private static Function<Parameter, String> buildParameterNameFunctionForMicronaut() {
    return parameter -> parameter.isAnnotationPresent(Named.class) ? parameter.getAnnotation(Named.class).value() : null;
  }

  ///////////////////////////////////////////////////
  // Build methods
  ///////////////////////////////////////////////////

  public SELF setMicronautContext(ApplicationContext micronautContext) {
    getDependencyManager().setContext(new MicronautDependencyContext(micronautContext));
    return getInstance();
  }

  public SELF setEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
    if (applicationEventPublisher == null) {
      throw new MongockException("EventPublisher cannot e null");
    }
    this.eventPublisher = new EventPublisher<>(
        () -> applicationEventPublisher.publishEvent(new MicronautMigrationStartedEvent(this)),
        result -> applicationEventPublisher.publishEvent(new MicrounautMigrationSuccessEvent<>(this, result)),
        result -> applicationEventPublisher.publishEvent(new MicronautMigrationFailureEvent(this, result))
    );
    return getInstance();
  }

  public MongockApplicationRunner buildApplicationRunner() {
    return new MongockApplicationRunner(buildRunner());
  }

//  public MongockInitializingBeanRunner buildInitializingBeanRunner() {
//    return new MongockInitializingBeanRunner(buildRunner());
//  }

//  @Override
//  protected Function<AnnotatedElement, Boolean> getAnnotationFilter() {
//    DependencyContext dependencyContext = getDependencyManager().getDependencyContext();
//    ApplicationContext micronautContext = ((MicronautDependencyContext) dependencyContext).getMicronautContext();
//    return annotated -> ProfileUtil.matchesActiveEnvironmentProfile(
//        getActiveProfilesFromContext(micronautContext),
//        Environment.class,
//        annotated,
//        (AnnotatedElement element) -> element.getAnnotation(Environment.class).value());
//  }

  @Override
  protected void validateConfigurationAndInjections(ConnectionDriver<CHANGE_ENTRY> driver) {
    super.validateConfigurationAndInjections(driver);
    if (!(getDependencyManager()).isContextPresent()) {
      throw new MongockException("ApplicationContext from Spring must be injected to Builder");
    }
  }

  //TODO: refactor with common-base?
  public DependencyManagerWithContext getDependencyManager() {
    return (DependencyManagerWithContext) dependencyManager;
  }
}
