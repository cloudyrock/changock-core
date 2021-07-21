package com.github.cloudyrock.mongock.micronaut.base.config;

import com.github.cloudyrock.mongock.config.MongockConfiguration;
import com.github.cloudyrock.mongock.driver.api.driver.ConnectionDriver;
import com.github.cloudyrock.mongock.driver.api.entry.ChangeEntry;
import com.github.cloudyrock.mongock.micronaut.base.MongockApplicationRunner;
import com.github.cloudyrock.mongock.micronaut.base.builder.MicronautApplicationBean;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.event.ApplicationEventPublisher;


public abstract class MongockContextBase<CHANGE_ENTRY extends ChangeEntry, CONFIG extends MongockConfiguration> {

  @Bean
  public MongockApplicationRunner applicationRunner(ConnectionDriver<CHANGE_ENTRY> connectionDriver,
                                                    CONFIG config,
                                                    ApplicationContext applicationContext,
                                                    ApplicationEventPublisher applicationEventPublisher) {
    return getBuilder(connectionDriver, config, applicationContext, applicationEventPublisher).buildApplicationRunner();
  }

//  @Bean
//  @ConditionalOnExpression("'${mongock.runner-type:null}'.equals('InitializingBean')")
//  public MongockInitializingBeanRunner initializingBeanRunner(ConnectionDriver<CHANGE_ENTRY> connectionDriver,
//                                                              CONFIG springConfiguration,
//                                                              ApplicationContext springContext,
//                                                              ApplicationEventPublisher applicationEventPublisher) {
//    return getBuilder(connectionDriver, springConfiguration, springContext, applicationEventPublisher)
//        .buildInitializingBeanRunner();
//  }

  @SuppressWarnings("all")
  public abstract MicronautApplicationBean getBuilder(ConnectionDriver<CHANGE_ENTRY> connectionDriver,
                                                      CONFIG springConfiguration,
                                                      ApplicationContext springContext,
                                                      ApplicationEventPublisher applicationEventPublisher);
}


