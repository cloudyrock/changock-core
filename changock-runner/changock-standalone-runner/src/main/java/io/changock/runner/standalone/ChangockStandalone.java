package io.changock.runner.standalone;

import io.changock.driver.api.driver.ConnectionDriver;
import io.changock.runner.core.ChangeLogService;
import io.changock.runner.core.ChangockBase;
import io.changock.runner.core.MigrationExecutor;
import io.changock.runner.core.builder.ChangockConfiguration;
import io.changock.runner.core.builder.DriverBuilderConfigurable;
import io.changock.runner.core.builder.RunnerBuilderBase;

public class ChangockStandalone {

  public static DriverBuilderConfigurable<Builder, ConnectionDriver> builder() {
    return new Builder();
  }


  public static class Builder extends RunnerBuilderBase<Builder, ConnectionDriver> {

    private Builder() {
    }

    public ChangockStandaloneRunner build() {
      return new ChangockStandaloneRunner(buildExecutorDefault(), buildChangeLogServiceDefault(), throwExceptionIfCannotObtainLock, enabled);
    }

    @Override
    protected Builder returnInstance() {
      return this;
    }


  }

  public static class ChangockStandaloneRunner extends ChangockBase {

    protected ChangockStandaloneRunner(MigrationExecutor executor, ChangeLogService changeLogService, boolean throwExceptionIfCannotObtainLock, boolean enabled) {
      super(executor, changeLogService, throwExceptionIfCannotObtainLock, enabled);
    }
  }
}
