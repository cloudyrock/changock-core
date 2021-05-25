package com.github.cloudyrock.springboot.base.builder.migration;

import com.github.cloudyrock.mongock.config.MongockConfiguration;
import com.github.cloudyrock.mongock.runner.core.builder.MigrationBuilderBase;
import com.github.cloudyrock.springboot.base.MongockApplicationRunner;
import com.github.cloudyrock.springboot.base.MongockInitializingBeanRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;

//TODO javadoc
public interface MigrationSpringbootBuilderBase<BUILDER_TYPE extends MigrationSpringbootBuilderBase<BUILDER_TYPE, CONFIG>, CONFIG extends MongockConfiguration>
    extends MigrationBuilderBase<BUILDER_TYPE, Boolean, CONFIG> {

  //TODO javadoc
  BUILDER_TYPE setSpringContext(ApplicationContext springContext);

  //TODO javadoc
  BUILDER_TYPE setEventPublisher(ApplicationEventPublisher applicationEventPublisher);

  //TODO javadoc
  MongockApplicationRunner<Boolean> buildApplicationRunner();

  //TODO javadoc
  MongockInitializingBeanRunner<Boolean> buildInitializingBeanRunner();
}
