package com.github.cloudyrock.mongock.runner.core.executor.migration;

public class ExecutorConfiguration {

  private final boolean trackIgnored;
  private final String serviceIdentifier;

  public ExecutorConfiguration(boolean trackIgnored, String serviceIdentifier) {
    this.trackIgnored = trackIgnored;
    this.serviceIdentifier = serviceIdentifier;
  }

  public boolean isTrackIgnored() {
    return trackIgnored;
  }

  public String getServiceIdentifier() {
    return serviceIdentifier;
  }
}
