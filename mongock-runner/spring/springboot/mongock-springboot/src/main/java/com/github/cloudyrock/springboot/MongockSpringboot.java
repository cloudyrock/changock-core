package com.github.cloudyrock.springboot;


import com.github.cloudyrock.mongock.config.MongockConfiguration;
import com.github.cloudyrock.mongock.runner.core.executor.ExecutorFactory;
import com.github.cloudyrock.mongock.runner.core.executor.operation.Operation;
import com.github.cloudyrock.mongock.runner.core.executor.operation.change.MigrationBuilder;
import com.github.cloudyrock.mongock.runner.core.executor.operation.change.MigrationOp;
import com.github.cloudyrock.springboot.base.SpringbootBuilderBase;

public final class MongockSpringboot {

  //TODO javadoc
  public static MigrationBuilderImpl<Boolean> builder() {
    return new MigrationBuilderImpl<>(new MigrationOp(), new ExecutorFactory<>(), new MongockConfiguration());
  }


  public static class MigrationBuilderImpl<RETURN_TYPE> extends SpringbootBuilderBase<MigrationBuilderImpl<RETURN_TYPE>, RETURN_TYPE, MongockConfiguration>  {

    private MigrationBuilderImpl(Operation<RETURN_TYPE> op, ExecutorFactory<MongockConfiguration> executorFactory, MongockConfiguration config) {
      super(op, executorFactory, config);
    }

    @Override
    public MigrationBuilderImpl<RETURN_TYPE> getInstance() {
      return this;
    }
  }


}
