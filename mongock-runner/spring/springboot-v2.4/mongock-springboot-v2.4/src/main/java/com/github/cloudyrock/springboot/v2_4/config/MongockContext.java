package com.github.cloudyrock.springboot.v2_4.config;

import com.github.cloudyrock.mongock.driver.api.driver.ConnectionDriver;
import com.github.cloudyrock.springboot.v2_4.MongockSpringbootV2_4;
import com.github.cloudyrock.springboot.v2_4.SpringbootV2_4BuilderBase;
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
  public SpringbootV2_4BuilderBase.MongockApplicationRunnerBase applicationRunner(ConnectionDriver connectionDriver,
                                                      MongockSpringConfiguration springConfiguration,
                                                      ApplicationContext springContext,
                                                      ApplicationEventPublisher applicationEventPublisher) {
    return getBuilder(connectionDriver, springConfiguration, springContext, applicationEventPublisher)
        .buildApplicationRunner();
  }

  @Bean
  @ConditionalOnExpression("'${mongock.runner-type:null}'.equals('InitializingBean')")
  public SpringbootV2_4BuilderBase.MongockInitializingBeanRunnerBase initializingBeanRunner(ConnectionDriver connectionDriver,
                                                                                    MongockSpringConfiguration springConfiguration,
                                                                                    ApplicationContext springContext,
                                                                                    ApplicationEventPublisher applicationEventPublisher) {
    return getBuilder(connectionDriver, springConfiguration, springContext, applicationEventPublisher)
        .buildInitializingBeanRunner();
  }

  private MongockSpringbootV2_4.Builder getBuilder(ConnectionDriver connectionDriver,
                                                   MongockSpringConfiguration springConfiguration,
                                                   ApplicationContext springContext,
                                                   ApplicationEventPublisher applicationEventPublisher) {
    //TODO check if professional in classPath,
    return MongockSpringbootV2_4.builder()
        .setDriver(connectionDriver)
        .setConfig(springConfiguration)
        .setSpringContext(springContext)
        .setEventPublisher(applicationEventPublisher);
  }
}


