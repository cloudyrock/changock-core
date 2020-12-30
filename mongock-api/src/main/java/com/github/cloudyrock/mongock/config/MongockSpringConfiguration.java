package com.github.cloudyrock.mongock.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("mongock")
public class MongockSpringConfiguration extends MongockConfiguration {
  /**
   * Type of Spring bean Mongock should be: ApplicationRunner(default) or InitializingBean
   */
  private SpringRunnerType runnerType = SpringRunnerType.ApplicationRunner;

  public SpringRunnerType getRunnerType() {
    return runnerType;
  }

  public void setRunnerType(SpringRunnerType runnerType) {
    this.runnerType = runnerType;
  }
}
