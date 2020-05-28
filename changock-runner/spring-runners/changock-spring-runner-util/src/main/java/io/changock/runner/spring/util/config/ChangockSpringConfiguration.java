package io.changock.runner.spring.util.config;

import io.changock.runner.core.builder.ChangockConfiguration;

public abstract class ChangockSpringConfiguration extends ChangockConfiguration {
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
