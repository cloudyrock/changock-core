package com.github.cloudyrock.springboot;

import com.github.cloudyrock.mongock.config.MongockConfiguration;
import com.github.cloudyrock.mongock.runner.core.executor.ExecutorFactory;
import com.github.cloudyrock.mongock.runner.core.executor.operation.Operation;
import com.github.cloudyrock.mongock.runner.core.executor.operation.change.MigrationOp;
import com.github.cloudyrock.springboot.base.SpringbootBuilderBase;

public final class MongockSpringboot {

  //TODO javadoc
  public static CommunitySpringBootBuilder<Boolean> builder() {
    return new CommunitySpringBootBuilder<>(new MigrationOp(), new ExecutorFactory<>(), new MongockConfiguration());
  }

  public static class CommunitySpringBootBuilder<RETURN_TYPE> extends SpringbootBuilderBase<CommunitySpringBootBuilder<RETURN_TYPE>, RETURN_TYPE, MongockConfiguration> {
    private CommunitySpringBootBuilder(Operation<RETURN_TYPE> op, ExecutorFactory<MongockConfiguration> executorFactory, MongockConfiguration config) {
      super(op, executorFactory, config);
    }

    @Override
    public CommunitySpringBootBuilder<RETURN_TYPE> getInstance() {
      return this;
    }
  }


}
