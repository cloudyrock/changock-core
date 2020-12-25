package io.changock.runner.core.executor;

public class MigrationExecutorConfiguration {

  private final boolean trackIgnored;

  public MigrationExecutorConfiguration(boolean trackIgnored) {
    this.trackIgnored = trackIgnored;
  }

  public boolean isTrackIgnored() {
    return trackIgnored;
  }
}
