package com.github.cloudyrock.standalone;

import com.github.cloudyrock.mongock.config.MongockConfiguration;
import com.github.cloudyrock.mongock.runner.core.executor.ExecutorFactory;
import com.github.cloudyrock.mongock.runner.core.executor.operation.change.MigrationOp;
import com.github.cloudyrock.standalone.migration.MigrationStandaloneBuilder;

public final class MongockStandalone {

  //TODO javadoc
  public static MigrationStandaloneBuilder builder() {
    return new MigrationBuilderImpl(new ExecutorFactory<>(), new MongockConfiguration());
  }

  public static class MigrationBuilderImpl extends StandaloneBuilderBase<MigrationBuilderImpl, Boolean, MongockConfiguration>
      implements MigrationStandaloneBuilder {

    private MigrationBuilderImpl(ExecutorFactory<MongockConfiguration> executorFactory, MongockConfiguration config) {
      super(new MigrationOp(), executorFactory, config);
    }

    @Override
    public MigrationBuilderImpl getInstance() {
      return this;
    }
  }


}
