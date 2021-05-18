package com.github.cloudyrock.springboot;


import com.github.cloudyrock.mongock.runner.core.executor.ExecutorFactory;
import com.github.cloudyrock.mongock.runner.core.executor.change.MigrationOp;
import com.github.cloudyrock.springboot.base.SpringbootBuilderBase;
import com.github.cloudyrock.springboot.config.MongockSpringConfiguration;

public final class MongockSpringboot {


  //TODO javadoc
  public static Builder builder() {
    return new Builder(new ExecutorFactory<>(), new MongockSpringConfiguration());
  }

  public static class Builder extends SpringbootBuilderBase<Builder, MongockSpringConfiguration, MongockSpringConfiguration> {


    private Builder(ExecutorFactory<MongockSpringConfiguration> executorFactory, MongockSpringConfiguration config) {
      super(executorFactory, config);
    }

    //TODO javadoc
    @SuppressWarnings("unchecked")
    public MongockApplicationRunner buildApplicationRunner() {
      this.runner = buildRunner(new MigrationOp());
      return args -> runner.execute();
    }


    //TODO javadoc
    @SuppressWarnings("unchecked")
    public MongockInitializingBeanRunner buildInitializingBeanRunner() {
      this.runner = buildRunner(new MigrationOp());
      return () -> runner.execute();
    }

    @Override
    protected Builder getInstance() {
      return this;
    }

    @Override
    protected MongockSpringConfiguration getExecutorConfig() {
      return config;
    }
  }


  @FunctionalInterface
  public interface MongockApplicationRunner extends SpringbootBuilderBase.MongockApplicationRunnerBase {
  }

  @FunctionalInterface
  public interface MongockInitializingBeanRunner extends SpringbootBuilderBase.MongockInitializingBeanRunnerBase {
  }

}
