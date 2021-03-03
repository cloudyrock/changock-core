package com.github.cloudyrock.spring.v5.config.test;

import com.github.cloudyrock.mongock.driver.api.driver.ConnectionDriver;
import com.github.cloudyrock.spring.v5.MongockSpring5;
import com.github.cloudyrock.spring.v5.config.MongockDriverContextSelector;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Deprecated//since=4.3.6, forRemoval=true
@Import(MongockDriverContextSelector.class)
@ConditionalOnProperty(prefix = "mongock", name = "enabled", matchIfMissing = true, havingValue = "true")
public class MongockTestContext {


  @Bean
  @ConditionalOnMissingBean({MongockSpring5.MongockInitializingBeanRunner.class, MongockSpring5.MongockApplicationRunner.class})
  public TestDriverInitializingBean testDriverInitializingBean(ConnectionDriver connectionDriver) {
    return new TestDriverInitializingBean(connectionDriver);
  }


  public static class TestDriverInitializingBean implements InitializingBean {

    private final ConnectionDriver driver;

    private TestDriverInitializingBean(ConnectionDriver driver) {
      this.driver = driver;
    }

    @Override
    public void afterPropertiesSet() {
      // As it's a test environment we need to ensure the lock is released before acquiring it
      driver.getLockManager().clean();
      driver.getLockManager().acquireLockDefault();
    }
  }
}
