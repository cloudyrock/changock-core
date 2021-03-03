package com.github.cloudyrock.mongock.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


/**
 * It needs to be loaded explicitly in the Driver importer(example MongoSpringDataImporter.java)
 * So each driver can override the MongockSpringConfiguration in case it adds any new parameter
 */
@Configuration
@ConfigurationProperties("mongock")
public class MongockSpringConfiguration extends MongockConfiguration {


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
