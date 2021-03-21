package com.github.cloudyrock.springboot.v2_2.config;

import com.github.cloudyrock.mongock.config.MongockSpringConfiguration;
import com.github.cloudyrock.mongock.driver.api.driver.ConnectionDriver;
import com.github.cloudyrock.springboot.v2_2.MongockSpringbootV2_2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import(MongockDriverContextSelector.class)
@ConditionalOnExpression("${mongock.enabled:true}")
public class MongockContext {

  @Bean
  @ConditionalOnExpression("'${mongock.runner-type:ApplicationRunner}'.equals('ApplicationRunner')")
  public MongockSpringbootV2_2.MongockApplicationRunner applicationRunner(ConnectionDriver connectionDriver,
                                                     MongockSpringConfiguration springConfiguration,
                                                     ApplicationContext springContext,
                                                     ApplicationEventPublisher applicationEventPublisher) {
    return getBuilder(connectionDriver, springConfiguration, springContext, applicationEventPublisher)
        .buildApplicationRunner();
  }

  @Bean
  @ConditionalOnExpression("'${mongock.runner-type:null}'.equals('InitializingBean')")
  public MongockSpringbootV2_2.MongockInitializingBeanRunner initializingBeanRunner(ConnectionDriver connectionDriver,
                                                                                            MongockSpringConfiguration springConfiguration,
                                                                                            ApplicationContext springContext,
                                                                                            ApplicationEventPublisher applicationEventPublisher) {
    return getBuilder(connectionDriver, springConfiguration, springContext, applicationEventPublisher)
        .buildInitializingBeanRunner();
  }

  private MongockSpringbootV2_2.Builder getBuilder(ConnectionDriver connectionDriver,
                                                   MongockSpringConfiguration springConfiguration,
                                                   ApplicationContext springContext,
                                                   ApplicationEventPublisher applicationEventPublisher) {
    return MongockSpringbootV2_2.builder()
        .setDriver(connectionDriver)
        .setConfig(springConfiguration)
        .setSpringContext(springContext)
        .setEventPublisher(applicationEventPublisher);
  }
}


