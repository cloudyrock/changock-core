package com.github.cloudyrock.springboot.base.config;

import com.github.cloudyrock.mongock.driver.api.driver.ConnectionDriver;
import com.github.cloudyrock.spring.config.MongockSpringConfigurationBase;
import com.github.cloudyrock.springboot.base.SpringbootBuilderBase;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;


public abstract class MongockContextBase<CONFIG extends MongockSpringConfigurationBase> {

  @Bean
  @ConditionalOnExpression("'${mongock.runner-type:ApplicationRunner}'.equals('ApplicationRunner')")
  public SpringbootBuilderBase.MongockApplicationRunnerBase applicationRunner(ConnectionDriver connectionDriver,
                                                                              CONFIG springConfiguration,
                                                                              ApplicationContext springContext,
                                                                              ApplicationEventPublisher applicationEventPublisher) {
    return getBuilder(connectionDriver, springConfiguration, springContext, applicationEventPublisher)
        .buildApplicationRunner();
  }

  @Bean
  @ConditionalOnExpression("'${mongock.runner-type:null}'.equals('InitializingBean')")
  public SpringbootBuilderBase.MongockInitializingBeanRunnerBase initializingBeanRunner(ConnectionDriver connectionDriver,
                                                                                        CONFIG springConfiguration,
                                                                                        ApplicationContext springContext,
                                                                                        ApplicationEventPublisher applicationEventPublisher) {
    return getBuilder(connectionDriver, springConfiguration, springContext, applicationEventPublisher)
        .buildInitializingBeanRunner();
  }

  protected abstract SpringbootBuilderBase getBuilder(ConnectionDriver connectionDriver,
                                                      CONFIG springConfiguration,
                                                      ApplicationContext springContext,
                                                      ApplicationEventPublisher applicationEventPublisher);
}


