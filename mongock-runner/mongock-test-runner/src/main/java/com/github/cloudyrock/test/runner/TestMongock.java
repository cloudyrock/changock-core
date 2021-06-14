package com.github.cloudyrock.test.runner;

import com.github.cloudyrock.mongock.ChangeLogItem;
import com.github.cloudyrock.mongock.ChangeSetItem;
import com.github.cloudyrock.mongock.config.MongockConfiguration;
import com.github.cloudyrock.mongock.driver.api.entry.ChangeEntry;
import com.github.cloudyrock.mongock.runner.core.builder.MigrationBuilderBase;
import com.github.cloudyrock.mongock.runner.core.builder.RunnerBuilderBase;
import com.github.cloudyrock.mongock.runner.core.executor.ExecutorFactory;
import com.github.cloudyrock.mongock.runner.core.executor.ExecutorFactoryDefault;
import com.github.cloudyrock.mongock.runner.core.executor.changelog.ChangeLogService;
import com.github.cloudyrock.mongock.runner.core.executor.dependency.DependencyManager;
import com.github.cloudyrock.mongock.runner.core.executor.operation.change.MigrationOp;

public class TestMongock {

  public static Builder builder() {
    return new Builder(new ExecutorFactoryDefault<>());
  }

  public static class Builder extends RunnerBuilderBase<Builder, Boolean, ChangeLogItem<ChangeSetItem>, ChangeSetItem, ChangeEntry, MongockConfiguration> implements MigrationBuilderBase<Builder, ChangeEntry, Boolean, MongockConfiguration> {

    private Builder(ExecutorFactory<ChangeLogItem<ChangeSetItem>, ChangeSetItem, ChangeEntry, MongockConfiguration, Boolean> executorFactory) {
      super(new MigrationOp(), executorFactory, new ChangeLogService(), new DependencyManager(), new MongockConfiguration());
    }

//    @Override
//    protected void beforeBuildRunner() {
//
//    }

    @Override
    public Builder getInstance() {
      return this;
    }

  }
}
