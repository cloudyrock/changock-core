package com.github.cloudyrock.springboot.v2_2;

import com.github.cloudyrock.mongock.config.MongockSpringConfiguration;
import com.github.cloudyrock.mongock.driver.api.driver.ConnectionDriver;
import com.github.cloudyrock.mongock.runner.core.builder.DriverBuilderConfigurable;
import com.github.cloudyrock.mongock.runner.core.executor.DefaultDependencyContext;
import com.github.cloudyrock.mongock.runner.core.executor.MigrationExecutor;
import com.github.cloudyrock.mongock.runner.core.executor.MigrationExecutorConfiguration;
import com.github.cloudyrock.mongock.runner.core.executor.MongockRunnerBase;
import com.github.cloudyrock.mongock.utils.CollectionUtils;
import com.github.cloudyrock.spring.util.MongockSpringBuilderBase;
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

public final class MongockSpringbootV2_2 {


  public static DriverBuilderConfigurable<Builder, ConnectionDriver, MongockSpringConfiguration> builder() {
    return new Builder();
  }

  public static class Builder extends MongockSpringBuilderBase<Builder> {


    private List<String> activeProfiles;

    private static final String DEFAULT_PROFILE = "default";

    private Builder() {
    }

    public Builder setSpringContext(ApplicationContext springContext) {
      addDependencyManager(new DefaultDependencyContext(springContext::getBean, springContext::getBean));
      setActiveProfilesFromContext(springContext);
      return getInstance();
    }

    public Builder setEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
      return setEventPublisher(new SpringEventPublisher(applicationEventPublisher));
    }

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

    public MongockApplicationRunner buildApplicationRunner() {
      return args -> getRunner().execute();
    }

    public MongockInitializingBeanRunner buildInitializingBeanRunner() {
      return () -> getRunner().execute();
    }

    private MongockRunnerBase getRunner() {
      runValidation();
      MigrationExecutor executor = new SpringMigrationExecutor(driver, dependencyManager, new MigrationExecutorConfiguration(trackIgnored), metadata);
      return new MongockRunnerBase(executor, getChangeLogService(), throwExceptionIfCannotObtainLock, enabled, applicationEventPublisher);
    }

    @Override
    protected Builder getInstance() {
      return this;
    }
  }

  @FunctionalInterface
  public interface MongockApplicationRunner extends ApplicationRunner { }

  @FunctionalInterface
  public interface MongockInitializingBeanRunner extends InitializingBean { }
}
