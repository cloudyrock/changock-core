package com.github.cloudyrock.springboot;


import com.github.cloudyrock.mongock.config.MongockConfiguration;
import com.github.cloudyrock.mongock.runner.core.executor.ExecutorFactory;
import com.github.cloudyrock.mongock.runner.core.executor.operation.Operation;
import com.github.cloudyrock.mongock.runner.core.executor.operation.change.MigrationBuilder;
import com.github.cloudyrock.mongock.runner.core.executor.operation.change.MigrationOp;
import com.github.cloudyrock.springboot.base.SpringbootBuilderBase;

public final class MongockSpringboot {


  //TODO javadoc
  public static MigrationBuilder<MigrationBuilderImpl, MongockConfiguration> builder() {
    return new MigrationBuilderImpl(new ExecutorFactory<>(), new MongockConfiguration());
  }



  public static class MigrationBuilderImpl extends Builder<MigrationBuilderImpl, Boolean> implements MigrationBuilder<MigrationBuilderImpl, MongockConfiguration> {

    private MigrationBuilderImpl(ExecutorFactory<MongockConfiguration> executorFactory, MongockConfiguration config) {
      super(new MigrationOp(), executorFactory, config);
    }

    @Override
    public MigrationBuilderImpl getInstance() {
      return this;
    }
  }

  public static abstract class Builder<BUILDER_TYPE extends Builder, RETURN_TYPE> extends SpringbootBuilderBase<BUILDER_TYPE, RETURN_TYPE, MongockConfiguration> {


    private Builder(Operation<RETURN_TYPE> operation, ExecutorFactory<MongockConfiguration> executorFactory, MongockConfiguration config) {
      super(operation, executorFactory, config);
    }

    //TODO javadoc
    @SuppressWarnings("unchecked")
    public MongockApplicationRunner buildApplicationRunner() {
      this.runner = buildRunner();
      return args -> runner.execute();
    }


    //TODO javadoc
    @SuppressWarnings("unchecked")
    public MongockInitializingBeanRunner buildInitializingBeanRunner() {
      this.runner = buildRunner();
      return () -> runner.execute();
    }


  }


  @FunctionalInterface
  public interface MongockApplicationRunner extends SpringbootBuilderBase.MongockApplicationRunnerBase {
  }

  @FunctionalInterface
  public interface MongockInitializingBeanRunner extends SpringbootBuilderBase.MongockInitializingBeanRunnerBase {
  }

}
