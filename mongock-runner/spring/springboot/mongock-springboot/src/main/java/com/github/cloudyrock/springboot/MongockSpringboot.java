package com.github.cloudyrock.springboot;


import com.github.cloudyrock.mongock.config.MongockConfiguration;
import com.github.cloudyrock.mongock.runner.core.executor.ExecutorFactory;
import com.github.cloudyrock.mongock.runner.core.executor.operation.change.MigrationBuilder;
import com.github.cloudyrock.mongock.runner.core.executor.operation.change.MigrationOp;
import com.github.cloudyrock.springboot.base.SpringbootBuilderBase;

public final class MongockSpringboot {

  //TODO javadoc
  public static MigrationBuilderImpl builder() {
    return new MigrationBuilderImpl(new ExecutorFactory<>(), new MongockConfiguration());
  }


  public static class MigrationBuilderImpl extends SpringbootBuilderBase<MigrationBuilderImpl, Boolean, MongockConfiguration> implements MigrationBuilder<MigrationBuilderImpl, MongockConfiguration> {

    private MigrationBuilderImpl(ExecutorFactory<MongockConfiguration> executorFactory, MongockConfiguration config) {
      super(new MigrationOp(), executorFactory, config);
    }

    @Override
    public MigrationBuilderImpl getInstance() {
      return this;
    }
  }


}
