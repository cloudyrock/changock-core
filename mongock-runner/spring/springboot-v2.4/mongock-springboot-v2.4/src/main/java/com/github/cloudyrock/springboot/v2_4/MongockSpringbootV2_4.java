package com.github.cloudyrock.springboot.v2_4;

import com.github.cloudyrock.mongock.exception.MongockException;
import com.github.cloudyrock.mongock.runner.core.executor.MigrationExecutor;
import com.github.cloudyrock.mongock.runner.core.executor.MongockRunner;
import com.github.cloudyrock.mongock.utils.CollectionUtils;
import com.github.cloudyrock.spring.util.ProfileUtil;
import com.github.cloudyrock.spring.util.RunnerSpringBuilderBase;
import com.github.cloudyrock.spring.util.SpringDependencyContext;
import com.github.cloudyrock.springboot.v2_4.events.SpringEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

public final class MongockSpringbootV2_4 {

  private static final Logger logger = LoggerFactory.getLogger(MongockSpringbootV2_4.class);
  private static final String PROFESSIONAL_BUILDER_CLASS = "io.mongock.professional.springboot.v2_4.ProfessionalBuilder";

  /**
   * Factory method returning the standalone builder implementation
   *
   * @return the standalone builder implementation
   */
  public static Builder builder() {
    try {
      Builder proBuilderInstance = (Builder) Class.forName(PROFESSIONAL_BUILDER_CLASS).newInstance();
      logger.info("using MONGOCK PROFESSIONAL distribution");
      return proBuilderInstance;
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
      try {
        Builder communityBuilderInstance = Builder.class.newInstance();
        logger.info("using MONGOCK COMMUNITY distribution");
        return communityBuilderInstance;
      } catch (InstantiationException | IllegalAccessException ex) {
        throw new MongockException(ex);
      }
    }
  }

  public static class Builder extends RunnerSpringBuilderBase<Builder> {

    private ApplicationContext springContext;
    private List<String> activeProfiles;
    private MongockRunner runner;

    private static final String DEFAULT_PROFILE = "default";

    protected Builder() {
    }

    //TODO javadoc
    public Builder setSpringContext(ApplicationContext springContext) {
      this.springContext = springContext;
      addDependencyManager(new SpringDependencyContext(springContext));
      return getInstance();
    }

    //TODO javadoc
    public Builder setEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
      return setEventPublisher(new SpringEventPublisher(applicationEventPublisher));
    }

    //TODO javadoc
    public MongockApplicationRunner buildApplicationRunner() {
      this.runner = getRunner();
      return args -> runner.execute();
    }

    //TODO javadoc
    public MongockInitializingBeanRunner buildInitializingBeanRunner() {
      this.runner = getRunner();
      return () -> runner.execute();
    }

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


    private MongockRunner getRunner() {
      runValidation();
      setActiveProfilesFromContext(springContext);
      injectLegacyMigration();
      Function<Parameter, String> paramNameExtractor = Builder::getParameterName;
      MigrationExecutor executor = buildMigrationExecutor(paramNameExtractor);
      return new MongockRunner(executor, getChangeLogService(), throwExceptionIfCannotObtainLock, enabled, applicationEventPublisher);
    }


    private static String getParameterName(Parameter parameter) {
      String name = parameter.isAnnotationPresent(Named.class) ? parameter.getAnnotation(Named.class).value() : null;
      if (name == null) {
        name = parameter.isAnnotationPresent(Qualifier.class) ? parameter.getAnnotation(Qualifier.class).value() : null;
      }
      return name;
    }

    @Override
    protected Builder getInstance() {
      return this;
    }
  }

  @FunctionalInterface
  public interface MongockApplicationRunner extends ApplicationRunner {
  }

  @FunctionalInterface
  public interface MongockInitializingBeanRunner extends InitializingBean {
  }

}

