package com.github.cloudyrock.mongock.runner.core.executor.migration;

public class MigrationExecutorConfiguration {

  private final boolean trackIgnored;
  private final String serviceIdentifier;

  public MigrationExecutorConfiguration(boolean trackIgnored, String serviceIdentifier) {
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
