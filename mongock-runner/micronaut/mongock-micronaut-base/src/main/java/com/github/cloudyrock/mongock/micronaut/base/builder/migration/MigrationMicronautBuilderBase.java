package com.github.cloudyrock.mongock.micronaut.base.builder.migration;

import com.github.cloudyrock.mongock.config.MongockConfiguration;
import com.github.cloudyrock.mongock.driver.api.driver.ChangeSetDependency;
import com.github.cloudyrock.mongock.driver.api.entry.ChangeEntry;
import com.github.cloudyrock.mongock.micronaut.base.builder.MicronautApplicationBean;
import com.github.cloudyrock.mongock.runner.core.builder.MigrationBuilderBase;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.event.ApplicationEventPublisher;


public interface MigrationMicronautBuilderBase<
    SELF extends MigrationMicronautBuilderBase<SELF, CHANGE_ENTRY, CONFIG>,
    CHANGE_ENTRY extends ChangeEntry,
    CONFIG extends MongockConfiguration>
    extends
    MicronautApplicationBean,
    MigrationBuilderBase<SELF, CHANGE_ENTRY, Boolean, CONFIG> {

    //TODO javadoc
    SELF setMicronautContext(ApplicationContext applicationContext);

    //TODO javadoc
    SELF setEventPublisher(ApplicationEventPublisher applicationEventPublisher);

    @Override
    default
      SELF addDependency(String name, Class < ?>type, Object instance){
      getDependencyManager().addDriverDependency(new ChangeSetDependency(name, type, instance));
      return getInstance();
    }
  }
