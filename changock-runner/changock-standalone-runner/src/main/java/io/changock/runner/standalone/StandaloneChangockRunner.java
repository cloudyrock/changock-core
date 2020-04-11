package io.changock.runner.standalone;

import io.changock.runner.core.ChangeLogService;
import io.changock.runner.core.ChangockBase;
import io.changock.runner.core.MigrationExecutor;
import io.changock.runner.core.builder.DriverBuilderConfigurable;
import io.changock.runner.core.builder.RunnerBuilderBase;

public class StandaloneChangockRunner extends ChangockBase {

    public static DriverBuilderConfigurable<Builder> builder() {
        return new Builder();
    }

    private StandaloneChangockRunner(MigrationExecutor executor, ChangeLogService changeLogService, boolean throwExceptionIfCannotObtainLock, boolean enabled) {
        super(executor, changeLogService, throwExceptionIfCannotObtainLock, enabled);
    }

    public static class Builder extends RunnerBuilderBase<Builder> {

        private Builder() {
        }

        public StandaloneChangockRunner build() {
            return new StandaloneChangockRunner(buildExecutorDefault(), buildChangeLogServiceDefault(), throwExceptionIfCannotObtainLock, enabled);
        }

        @Override
        protected Builder returnInstance() {
            return this;
        }
    }
}
