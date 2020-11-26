package io.changock.runner.spring.v5.config;

import io.changock.driver.api.driver.ConnectionDriver;
import io.changock.migration.api.config.ChangockSpringConfiguration;
import io.changock.runner.spring.v5.ChangockSpring5;
import io.changock.runner.spring.v5.SpringApplicationRunner;
import io.changock.runner.spring.v5.SpringInitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

//@Configuration
@Import(ChangockContextSelector.class)
@ConditionalOnProperty(prefix = "changock", name = "enabled", matchIfMissing = true, havingValue = "true")
public class ChangockContext {

  @Bean
  @ConditionalOnProperty(value = "config.runner-type", matchIfMissing = true, havingValue = "ApplicationRunner")
  public SpringApplicationRunner applicationRunner(ConnectionDriver connectionDriver,
                                                   ChangockSpringConfiguration springConfiguration,
                                                   ApplicationContext springContext,
                                                   ApplicationEventPublisher applicationEventPublisher) {
    return getBuilder(connectionDriver, springConfiguration, springContext, applicationEventPublisher)
        .buildApplicationRunner();
  }

  @Bean
  @ConditionalOnProperty(value = "config.runner-type", havingValue = "InitializingBean")
  public SpringInitializingBean initializingBeanRunner(ConnectionDriver connectionDriver,
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


