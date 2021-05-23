package com.github.cloudyrock.springboot;


import com.github.cloudyrock.mongock.config.MongockConfiguration;
import com.github.cloudyrock.mongock.runner.core.executor.ExecutorFactory;
import com.github.cloudyrock.mongock.runner.core.executor.operation.Operation;
import com.github.cloudyrock.mongock.runner.core.executor.operation.change.MigrationOp;
import com.github.cloudyrock.springboot.base.SpringbootBuilderBase;

public final class MongockSpringboot {

  //TODO javadoc
  public static SpringBootBuilder<Boolean> builder() {
    return new SpringBootBuilder<>(new MigrationOp(), new ExecutorFactory<>(), new MongockConfiguration());
  }


  public static class SpringBootBuilder<RETURN_TYPE> extends SpringbootBuilderBase<SpringBootBuilder<RETURN_TYPE>, RETURN_TYPE, MongockConfiguration>  {

    private SpringBootBuilder(Operation<RETURN_TYPE> op, ExecutorFactory<MongockConfiguration> executorFactory, MongockConfiguration config) {
      super(op, executorFactory, config);
    }

    @Override
    public SpringBootBuilder<RETURN_TYPE> getInstance() {
      return this;
    }
  }


}
