package io.changock.migration.api.config;

public class ChangockSpringConfiguration extends ChangockConfiguration {
  /**
   * Type of Spring bean Changock should be: ApplicationRunner(default) or InitializingBean
   */
  private SpringRunnerType runnerType = SpringRunnerType.ApplicationRunner;

  public SpringRunnerType getRunnerType() {
    return runnerType;
  }

  public void setRunnerType(SpringRunnerType runnerType) {
    this.runnerType = runnerType;
  }
}
