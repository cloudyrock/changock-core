package com.github.cloudyrock.standalone;

import com.github.cloudyrock.mongock.config.MongockConfiguration;
import com.github.cloudyrock.mongock.runner.core.executor.ExecutorFactory;
import com.github.cloudyrock.mongock.runner.core.executor.operation.Operation;
import com.github.cloudyrock.mongock.runner.core.executor.operation.change.MigrationOp;

public final class MongockStandalone {

  //TODO javadoc
  public static Builder<Boolean> builder() {
    return new Builder<>(new MigrationOp(), new ExecutorFactory<>(), new MongockConfiguration());
  }

  public static class Builder<RETURN_TYPE> extends StandaloneBuilderBase<Builder<RETURN_TYPE>, RETURN_TYPE, MongockConfiguration> {
    private Builder(Operation<RETURN_TYPE> operation, ExecutorFactory<MongockConfiguration> executorFactory, MongockConfiguration config) {
      super(operation, executorFactory, config);
    }


    @Override
    public Builder<RETURN_TYPE> getInstance() {
      return this;
    }


  }

}
