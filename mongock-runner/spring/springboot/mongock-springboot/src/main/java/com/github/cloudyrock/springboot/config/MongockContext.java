package com.github.cloudyrock.springboot.config;

import com.github.cloudyrock.mongock.config.MongockConfiguration;
import com.github.cloudyrock.mongock.driver.api.driver.ConnectionDriver;
import com.github.cloudyrock.mongock.driver.api.entry.ChangeEntry;
import com.github.cloudyrock.springboot.MigrationSpringbootBuilder;
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
public class MongockContext extends MongockContextBase<ChangeEntry, MongockConfiguration> {


  @Bean
  public MigrationSpringbootBuilder getBuilder(ConnectionDriver<ChangeEntry> connectionDriver,
                                               MongockConfiguration springConfiguration,
                                               ApplicationContext springContext,
                                               ApplicationEventPublisher applicationEventPublisher) {
    return MongockSpringboot.builder()
        .setDriver(connectionDriver)
        .setConfig(springConfiguration)
        .setSpringContext(springContext)
        .setEventPublisher(applicationEventPublisher);
  }
}


