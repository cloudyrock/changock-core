package com.github.cloudyrock.springboot.v2_2.config.test;

import com.github.cloudyrock.mongock.driver.api.driver.ConnectionDriver;
import com.github.cloudyrock.springboot.v2_2.MongockSpringbootV2_2;
import com.github.cloudyrock.springboot.v2_2.config.MongockDriverContextSelector;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import(MongockDriverContextSelector.class)
@ConditionalOnProperty(prefix = "mongock", name = "enabled", matchIfMissing = true, havingValue = "true")
public class MongockTestContext {


  @Bean
  @ConditionalOnMissingBean({MongockSpringbootV2_2.MongockInitializingBeanRunner.class, MongockSpringbootV2_2.MongockApplicationRunner.class})
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
