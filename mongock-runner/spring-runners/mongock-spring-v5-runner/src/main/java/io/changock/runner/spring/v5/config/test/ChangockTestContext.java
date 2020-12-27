package io.changock.runner.spring.v5.config.test;

import com.github.cloudyrock.mongock.driver.api.driver.ConnectionDriver;
import io.changock.runner.spring.v5.SpringApplicationRunner;
import io.changock.runner.spring.v5.SpringInitializingBeanRunner;
import io.changock.runner.spring.v5.config.ChangockContextSelector;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import(ChangockContextSelector.class)
@ConditionalOnProperty(prefix = "changock", name = "enabled", matchIfMissing = true, havingValue = "true")
public class ChangockTestContext {


  @Bean
  @ConditionalOnMissingBean({SpringInitializingBeanRunner.class, SpringApplicationRunner.class})
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
