package com.github.cloudyrock.standalone;

import com.github.cloudyrock.mongock.ChangeLogItem;
import com.github.cloudyrock.mongock.config.MongockConfiguration;
import com.github.cloudyrock.mongock.runner.core.executor.ExecutorFactory;
import com.github.cloudyrock.mongock.runner.core.executor.changelog.ChangeLogService;
import com.github.cloudyrock.mongock.runner.core.executor.changelog.ChangeLogServiceBase;
import com.github.cloudyrock.mongock.runner.core.executor.operation.change.MigrationOp;

public final class MongockStandalone {

  //TODO javadoc
  public static MigrationStandaloneBuilder builder() {
    return new MigrationBuilderImpl(new ExecutorFactory<>(), new MongockConfiguration());
  }

  public static class MigrationBuilderImpl extends StandaloneBuilderBase<MigrationBuilderImpl, Boolean, ChangeLogItem, MongockConfiguration>
      implements MigrationStandaloneBuilder
  {

    private MigrationBuilderImpl(ExecutorFactory<ChangeLogItem, MongockConfiguration, Boolean> executorFactory, MongockConfiguration config) {
      super(new MigrationOp(), executorFactory, new ChangeLogService(), config);
    }

    @Override
    public MigrationBuilderImpl getInstance() {
      return this;
    }
  }


}
