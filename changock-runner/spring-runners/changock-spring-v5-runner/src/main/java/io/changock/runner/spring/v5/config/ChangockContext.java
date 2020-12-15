package io.changock.runner.spring.v5.config;

import io.changock.driver.api.driver.ConnectionDriver;
import io.changock.migration.api.config.ChangockSpringConfiguration;
import io.changock.runner.spring.v5.ChangockSpring5;
import io.changock.runner.spring.v5.SpringApplicationRunner;
import io.changock.runner.spring.v5.SpringInitializingBeanRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

//@Configuration
@Import(ChangockContextSelector.class)
@ConditionalOnExpression("${changock.enabled:true}")
public class ChangockContext {

  @Bean
  @ConditionalOnExpression("'${changock.runner-type:ApplicationRunner}'.equals('ApplicationRunner')")
  public SpringApplicationRunner applicationRunner(ConnectionDriver connectionDriver,
                                                   ChangockSpringConfiguration springConfiguration,
                                                   ApplicationContext springContext,
                                                   ApplicationEventPublisher applicationEventPublisher) {
    return getBuilder(connectionDriver, springConfiguration, springContext, applicationEventPublisher)
        .buildApplicationRunner();
  }

  @Bean
  @ConditionalOnExpression("'${changock.runner-type:null}'.equals('InitializingBean')")
  public SpringInitializingBeanRunner initializingBeanRunner(ConnectionDriver connectionDriver,
                                                             ChangockSpringConfiguration springConfiguration,
                                                             ApplicationContext springContext,
                                                             ApplicationEventPublisher applicationEventPublisher) {
    return getBuilder(connectionDriver, springConfiguration, springContext, applicationEventPublisher)
        .buildInitializingBeanRunner();
  }


  private ChangockSpring5.Builder getBuilder(ConnectionDriver connectionDriver,
                                             ChangockSpringConfiguration springConfiguration,
                                             ApplicationContext springContext,
                                             ApplicationEventPublisher applicationEventPublisher) {
    return ChangockSpring5.builder()
        .setDriver(connectionDriver)
        .setConfig(springConfiguration)
        .setSpringContext(springContext)
        .setEventPublisher(applicationEventPublisher);
  }
}


