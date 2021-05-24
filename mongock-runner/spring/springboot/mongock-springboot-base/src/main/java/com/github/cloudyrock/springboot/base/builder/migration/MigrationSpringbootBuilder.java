package com.github.cloudyrock.springboot.base.builder.migration;

import com.github.cloudyrock.mongock.config.MongockConfiguration;
import com.github.cloudyrock.mongock.runner.core.builder.migration.MigrationBuilderBase;
import com.github.cloudyrock.springboot.base.MongockApplicationRunner;
import com.github.cloudyrock.springboot.base.MongockInitializingBeanRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;

//TODO javadoc
public interface MigrationSpringbootBuilder extends MigrationSpringbootBuilderBase<MigrationSpringbootBuilder, MongockConfiguration> {
}
