package com.github.cloudyrock.springboot.v2_2;

import com.github.cloudyrock.mongock.runner.core.executor.DefaultDependencyContext;
import com.github.cloudyrock.mongock.utils.CollectionUtils;
import com.github.cloudyrock.spring.util.ProfileUtil;
import com.github.cloudyrock.springboot.v2_2.events.SpringEventPublisher;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public abstract class MongockSpringBuilder<BUILDER_TYPE extends MongockSpringBuilder>
    extends MongockSpringBuilderBase<BUILDER_TYPE> {


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


  @Override
  protected Function<AnnotatedElement, Boolean> getAnnotationFilter() {
    return annotated -> ProfileUtil.matchesActiveSpringProfile(
        activeProfiles,
        Profile.class,
        annotated,
        (AnnotatedElement element) ->element.getAnnotation(Profile.class).value());
  }

}


