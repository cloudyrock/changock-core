package com.github.cloudyrock.spring.v5.config;

import com.github.cloudyrock.mongock.driver.api.driver.ConnectionDriver;
import com.github.cloudyrock.mongock.config.MongockSpringConfiguration;
import com.github.cloudyrock.mongock.driver.api.driver.TestDriverConnection;
import com.github.cloudyrock.spring.v5.MongockSpring5;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({MongockSpringConfiguration.class, MongockDriverContextSelector.class})
@ConditionalOnExpression("${mongock.enabled:true}")
public class MongockContext {

  @Bean
  @ConditionalOnExpression("'${mongock.runner-type:ApplicationRunner}'.equals('ApplicationRunner')")
  public MongockSpring5.MongockApplicationRunner applicationRunner(ConnectionDriver connectionDriver,
                                                                   MongockSpringConfiguration springConfiguration,
                                                                   ApplicationContext springContext,
                                                                   ApplicationEventPublisher applicationEventPublisher) {
    return getBuilder(connectionDriver, springConfiguration, springContext, applicationEventPublisher)
        .buildApplicationRunner();
  }

  @Bean
  @ConditionalOnExpression("'${mongock.runner-type:null}'.equals('InitializingBean')")
  public MongockSpring5.MongockInitializingBeanRunner initializingBeanRunner(ConnectionDriver connectionDriver,
                                                                             MongockSpringConfiguration springConfiguration,
                                                                             ApplicationContext springContext,
                                                                             ApplicationEventPublisher applicationEventPublisher) {
    return getBuilder(connectionDriver, springConfiguration, springContext, applicationEventPublisher)
        .buildInitializingBeanRunner();
  }

  @Bean
  @ConditionalOnExpression("${mongock.test-enabled:false}")
  public TestDriverConnection testDriverConnection(ConnectionDriver connectionDriver) {
    return new TestDriverConnection(connectionDriver);
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


