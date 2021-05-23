package com.github.cloudyrock.springboot.base;

import com.github.cloudyrock.mongock.config.LegacyMigration;
import com.github.cloudyrock.mongock.config.MongockConfiguration;
import com.github.cloudyrock.mongock.driver.api.driver.ChangeSetDependency;
import com.github.cloudyrock.mongock.exception.MongockException;
import com.github.cloudyrock.mongock.runner.core.builder.RunnerBuilderBase;
import com.github.cloudyrock.mongock.runner.core.event.MongockEventPublisher;
import com.github.cloudyrock.mongock.runner.core.executor.ExecutorFactory;
import com.github.cloudyrock.mongock.runner.core.executor.MongockRunner;
import com.github.cloudyrock.mongock.runner.core.executor.dependency.DependencyManagerWithContext;
import com.github.cloudyrock.mongock.runner.core.executor.operation.Operation;
import com.github.cloudyrock.mongock.utils.CollectionUtils;
import com.github.cloudyrock.spring.util.ProfileUtil;
import com.github.cloudyrock.springboot.base.context.SpringDependencyContext;
import com.github.cloudyrock.springboot.base.events.SpringMigrationFailureEvent;
import com.github.cloudyrock.springboot.base.events.SpringMigrationStartedEvent;
import com.github.cloudyrock.springboot.base.events.SpringMigrationSuccessEvent;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
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
import java.util.Optional;
import java.util.function.Function;

import static com.github.cloudyrock.mongock.config.MongockConstants.LEGACY_MIGRATION_NAME;

public abstract class SpringbootBuilderBase<BUILDER_TYPE extends SpringbootBuilderBase<BUILDER_TYPE, RETURN_TYPE, CONFIG>, RETURN_TYPE, CONFIG extends MongockConfiguration>
    extends RunnerBuilderBase<BUILDER_TYPE, RETURN_TYPE, CONFIG> {


  private List<String> activeProfiles;
  private static final String DEFAULT_PROFILE = "default";

  protected SpringbootBuilderBase(Operation<RETURN_TYPE> operation, ExecutorFactory<CONFIG> executorFactory, CONFIG config) {
    super(operation, executorFactory, config);
  }

  //TODO javadoc
  public BUILDER_TYPE setSpringContext(ApplicationContext springContext) {
    setActiveProfilesFromContext(springContext);
    this.dependencyManager = new DependencyManagerWithContext(new SpringDependencyContext(springContext));
    return getInstance();
  }

  //TODO javadoc
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

  //TODO javadoc
  public MongockApplicationRunner buildApplicationRunner() {
    return new MongockApplicationRunner(buildRunner());
  }


  //TODO javadoc
  public MongockInitializingBeanRunner buildInitializingBeanRunner() {
    return new MongockInitializingBeanRunner(buildRunner());
  }


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
    if (config.getLegacyMigration() != null) {
      dependencyManager.addStandardDependency(
          new ChangeSetDependency(LEGACY_MIGRATION_NAME, LegacyMigration.class, config.getLegacyMigration())
      );
    }
  }

  @Override
  public BUILDER_TYPE addDependency(String name, Class type, Object instance) {
    dependencyManager.addDriverDependency(new ChangeSetDependency(name, type, instance));
    return getInstance();
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


  @SuppressWarnings("all")
  public class MongockApplicationRunner implements ApplicationRunner {

    private final MongockRunner<RETURN_TYPE> runner;
    protected Optional<RETURN_TYPE> result;

    public MongockApplicationRunner(MongockRunner<RETURN_TYPE> runner) {
      this.runner = runner;
    }

    public Optional<RETURN_TYPE> getResult() {
      return result;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
      result = runner.execute();
    }
  }

  @SuppressWarnings("all")
  public class MongockInitializingBeanRunner implements InitializingBean {

    private final MongockRunner<RETURN_TYPE> runner;
    protected Optional<RETURN_TYPE> result;

    public MongockInitializingBeanRunner(MongockRunner<RETURN_TYPE> runner) {
      this.runner = runner;
    }

    public Optional<RETURN_TYPE> getResult() {
      return result;
    }

    @Override
    public void afterPropertiesSet() {
      result = runner.execute();
    }
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
