package com.github.cloudyrock.springboot.v2_2.base.config;

import com.github.cloudyrock.mongock.driver.api.driver.ConnectionDriver;
import com.github.cloudyrock.springboot.v2_2.base.SpringbootV2_2BuilderBase;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;


public abstract class MongockContextBase<CONFIG extends MongockSpringConfiguration> {

  @Bean
  @ConditionalOnExpression("'${mongock.runner-type:ApplicationRunner}'.equals('ApplicationRunner')")
  public SpringbootV2_2BuilderBase.MongockApplicationRunnerBase applicationRunner(ConnectionDriver connectionDriver,
                                                                                  CONFIG springConfiguration,
                                                                                  ApplicationContext springContext,
                                                                                  ApplicationEventPublisher applicationEventPublisher) {
    return getBuilder(connectionDriver, springConfiguration, springContext, applicationEventPublisher)
        .buildApplicationRunner();
  }

  @Bean
  @ConditionalOnExpression("'${mongock.runner-type:null}'.equals('InitializingBean')")
  public SpringbootV2_2BuilderBase.MongockInitializingBeanRunnerBase initializingBeanRunner(ConnectionDriver connectionDriver,
                                                                                            CONFIG springConfiguration,
                                                                                            ApplicationContext springContext,
                                                                                            ApplicationEventPublisher applicationEventPublisher) {
    return getBuilder(connectionDriver, springConfiguration, springContext, applicationEventPublisher)
        .buildInitializingBeanRunner();
  }

  protected abstract SpringbootV2_2BuilderBase getBuilder(ConnectionDriver connectionDriver,
                                                          CONFIG springConfiguration,
                                                          ApplicationContext springContext,
                                                          ApplicationEventPublisher applicationEventPublisher);
}

