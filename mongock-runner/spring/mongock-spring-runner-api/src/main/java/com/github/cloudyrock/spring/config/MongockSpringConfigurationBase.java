package com.github.cloudyrock.spring.config;

import com.github.cloudyrock.mongock.config.MongockConfiguration;

/**
 * It needs to be loaded explicitly in the Driver importer(example MongoSpringDataImporter.java)
 * So each driver can override the MongockSpringConfiguration in case it adds any new parameter
 */
public class MongockSpringConfigurationBase extends MongockConfiguration {


  private boolean testEnabled = false;
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

  public boolean isTestEnabled() {
    return testEnabled;
  }

  public void setTestEnabled(boolean testEnabled) {
    this.testEnabled = testEnabled;
  }
}
