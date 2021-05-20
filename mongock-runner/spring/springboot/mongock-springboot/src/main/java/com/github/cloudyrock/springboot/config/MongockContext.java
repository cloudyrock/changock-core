package com.github.cloudyrock.springboot.config;

import com.github.cloudyrock.mongock.config.MongockConfiguration;
import com.github.cloudyrock.mongock.driver.api.driver.ConnectionDriver;
import com.github.cloudyrock.springboot.MongockSpringboot;
import com.github.cloudyrock.springboot.base.config.MongockContextBase;
import com.github.cloudyrock.springboot.base.config.MongockDriverContextSelector;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import(MongockDriverContextSelector.class)
@ConditionalOnExpression("${mongock.enabled:true}")
public class MongockContext extends MongockContextBase<MongockConfiguration> {


  @Bean
  public MongockSpringboot.MigrationBuilderImpl getBuilder(ConnectionDriver connectionDriver,
                                              MongockConfiguration springConfiguration,
                                              ApplicationContext springContext,
                                              ApplicationEventPublisher applicationEventPublisher) {
    return MongockSpringboot.migrationBuilder()
        .setDriver(connectionDriver)
        .setConfig(springConfiguration)
        .setSpringContext(springContext)
        .setEventPublisher(applicationEventPublisher);
  }
}


