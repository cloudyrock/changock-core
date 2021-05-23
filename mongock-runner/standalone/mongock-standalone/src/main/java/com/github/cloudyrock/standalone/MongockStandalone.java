package com.github.cloudyrock.standalone;

import com.github.cloudyrock.mongock.config.MongockConfiguration;
import com.github.cloudyrock.mongock.runner.core.executor.ExecutorFactory;
import com.github.cloudyrock.mongock.runner.core.executor.operation.Operation;
import com.github.cloudyrock.mongock.runner.core.executor.operation.change.MigrationOp;

public final class MongockStandalone {

  //TODO javadoc
  public static CommunityStandaloneBuilder<Boolean> builder() {
    return new CommunityStandaloneBuilder<>(new MigrationOp(), new ExecutorFactory<>(), new MongockConfiguration());
  }

  public static class CommunityStandaloneBuilder<RETURN_TYPE> extends StandaloneBuilderBase<CommunityStandaloneBuilder<RETURN_TYPE>, RETURN_TYPE, MongockConfiguration> {
    private CommunityStandaloneBuilder(Operation<RETURN_TYPE> operation, ExecutorFactory<MongockConfiguration> executorFactory, MongockConfiguration config) {
      super(operation, executorFactory, config);
    }

    @Override
    public CommunityStandaloneBuilder<RETURN_TYPE> getInstance() {
      return this;
    }
  }

}
