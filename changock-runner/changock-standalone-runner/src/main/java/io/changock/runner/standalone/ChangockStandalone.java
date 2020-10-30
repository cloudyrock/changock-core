package io.changock.runner.standalone;

import io.changock.driver.api.driver.ConnectionDriver;
import io.changock.runner.core.ChangeLogService;
import io.changock.runner.core.ChangockBase;
import io.changock.runner.core.EventPublisher;
import io.changock.runner.core.MigrationExecutor;
import io.changock.runner.core.builder.configuration.ChangockConfiguration;
import io.changock.runner.core.builder.DriverBuilderConfigurable;
import io.changock.runner.core.builder.RunnerBuilderBase;

import java.util.function.Consumer;

public class ChangockStandalone {

  public static DriverBuilderConfigurable<Builder, ConnectionDriver, ChangockConfiguration> builder() {
    return new Builder();
  }


  public static class Builder extends RunnerBuilderBase<Builder, ConnectionDriver, ChangockConfiguration> {

    private Runnable migrationSuccessListener;
    private Consumer<Exception> migrationFailedListener;

    private Builder() {
    }

    public Builder setMigrationSuccessListener(Runnable listener) {
      this.migrationSuccessListener = listener;
      return returnInstance();
    }

    public Builder setMigrationFailListener(Consumer<Exception> listener) {
      this.migrationFailedListener = listener;
      return returnInstance();
    }

    public ChangockStandaloneRunner build() {
      StandaloneEventPublisher eventPublisher = new StandaloneEventPublisher(migrationSuccessListener, migrationFailedListener);
      return new ChangockStandaloneRunner(buildExecutorDefault(), buildChangeLogServiceDefault(), throwExceptionIfCannotObtainLock, enabled, eventPublisher);
    }

    @Override
    protected Builder returnInstance() {
      return this;
    }


  }

  public static class ChangockStandaloneRunner extends ChangockBase {

    protected ChangockStandaloneRunner(MigrationExecutor executor, ChangeLogService changeLogService, boolean throwExceptionIfCannotObtainLock, boolean enabled, EventPublisher eventPublisher) {
      super(executor, changeLogService, throwExceptionIfCannotObtainLock, enabled, eventPublisher);
    }
  }
}
