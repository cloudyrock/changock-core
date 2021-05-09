package com.github.cloudyrock.springboot.v2_2;

import com.github.cloudyrock.mongock.runner.core.executor.MigrationExecutorImpl;
import com.github.cloudyrock.mongock.runner.core.executor.MongockRunner;
import com.github.cloudyrock.mongock.utils.CollectionUtils;
import com.github.cloudyrock.spring.util.ProfileUtil;
import com.github.cloudyrock.spring.util.RunnerSpringBuilderBase;
import com.github.cloudyrock.spring.util.SpringDependencyContext;
import com.github.cloudyrock.springboot.v2_2.events.SpringEventPublisher;
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

public abstract class SpringbootV2_2BuilderBase<BUILDER_TYPE extends SpringbootV2_2BuilderBase> extends RunnerSpringBuilderBase<BUILDER_TYPE> {

  protected ApplicationContext springContext;
  protected List<String> activeProfiles;
  protected MongockRunner runner;

  private static final String DEFAULT_PROFILE = "default";

  protected SpringbootV2_2BuilderBase() {
  }

  //TODO javadoc
  public BUILDER_TYPE setSpringContext(ApplicationContext springContext) {
    this.springContext = springContext;
    addDependencyManager(new SpringDependencyContext(springContext));
    return getInstance();
  }

  //TODO javadoc
  public BUILDER_TYPE setEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
    return setEventPublisher(new SpringEventPublisher(applicationEventPublisher));
  }


  //TODO javadoc
  public abstract <T extends MongockApplicationRunnerBase> T buildApplicationRunner();

  //TODO javadoc
  public abstract <T extends MongockInitializingBeanRunnerBase> T buildInitializingBeanRunner();

  ///////////////////////////////////////////////////
  // PRIVATE METHODS
  ///////////////////////////////////////////////////

  private void setActiveProfilesFromContext(ApplicationContext springContext) {
    Environment springEnvironment = springContext.getEnvironment();
    this.activeProfiles = springEnvironment != null && CollectionUtils.isNotNullOrEmpty(springEnvironment.getActiveProfiles())
        ? Arrays.asList(springEnvironment.getActiveProfiles())
        : Collections.singletonList(DEFAULT_PROFILE);
  }

  @Override
  protected Function<AnnotatedElement, Boolean> getAnnotationFilter() {
    return annotated -> ProfileUtil.matchesActiveSpringProfile(
        activeProfiles,
        Profile.class,
        annotated,
        (AnnotatedElement element) -> element.getAnnotation(Profile.class).value());
  }


  protected MongockRunner getRunner() {
    runValidation();
    setActiveProfilesFromContext(springContext);
    injectLegacyMigration();
    Function<Parameter, String> paramNameExtractor = SpringbootV2_2BuilderBase::getParameterName;
    MigrationExecutorImpl executor = buildMigrationExecutor(paramNameExtractor);
    return new MongockRunner(executor, getChangeLogService(), throwExceptionIfCannotObtainLock, enabled, applicationEventPublisher);
  }


  private static String getParameterName(Parameter parameter) {
    String name = parameter.isAnnotationPresent(Named.class) ? parameter.getAnnotation(Named.class).value() : null;
    if (name == null) {
      name = parameter.isAnnotationPresent(Qualifier.class) ? parameter.getAnnotation(Qualifier.class).value() : null;
    }
    return name;
  }

  @FunctionalInterface
  public interface MongockApplicationRunnerBase extends ApplicationRunner {
  }

  @FunctionalInterface
  public interface MongockInitializingBeanRunnerBase extends InitializingBean {
  }
}
