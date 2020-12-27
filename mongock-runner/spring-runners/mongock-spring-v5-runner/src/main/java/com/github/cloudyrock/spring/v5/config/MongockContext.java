package com.github.cloudyrock.spring.v5.config;

import com.github.cloudyrock.mongock.driver.api.driver.ConnectionDriver;
import com.github.cloudyrock.mongock.config.MongockSpringConfiguration;
import com.github.cloudyrock.spring.v5.MongockSpring5;
import com.github.cloudyrock.spring.v5.SpringApplicationRunner;
import com.github.cloudyrock.spring.v5.SpringInitializingBeanRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

//@Configuration
@Import(MongockContextSelector.class)
@ConditionalOnExpression("${changock.enabled:true}")
public class MongockContext {

  @Bean
  @ConditionalOnExpression("'${changock.runner-type:ApplicationRunner}'.equals('ApplicationRunner')")
  public SpringApplicationRunner applicationRunner(ConnectionDriver connectionDriver,
                                                   MongockSpringConfiguration springConfiguration,
                                                   ApplicationContext springContext,
                                                   ApplicationEventPublisher applicationEventPublisher) {
    return getBuilder(connectionDriver, springConfiguration, springContext, applicationEventPublisher)
        .buildApplicationRunner();
  }

  @Bean
  @ConditionalOnExpression("'${changock.runner-type:null}'.equals('InitializingBean')")
  public SpringInitializingBeanRunner initializingBeanRunner(ConnectionDriver connectionDriver,
                                                             MongockSpringConfiguration springConfiguration,
                                                             ApplicationContext springContext,
                                                             ApplicationEventPublisher applicationEventPublisher) {
    return getBuilder(connectionDriver, springConfiguration, springContext, applicationEventPublisher)
        .buildInitializingBeanRunner();
  }


  private MongockSpring5.Builder getBuilder(ConnectionDriver connectionDriver,
                                            MongockSpringConfiguration springConfiguration,
                                            ApplicationContext springContext,
                                            ApplicationEventPublisher applicationEventPublisher) {
    return MongockSpring5.builder()
        .setDriver(connectionDriver)
        .setConfig(springConfiguration)
        .setSpringContext(springContext)
        .setEventPublisher(applicationEventPublisher);
  }
}


