package io.changock.migration.api.config;

import io.changock.migration.api.config.ChangockConfiguration;
import io.changock.migration.api.config.SpringRunnerType;

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
