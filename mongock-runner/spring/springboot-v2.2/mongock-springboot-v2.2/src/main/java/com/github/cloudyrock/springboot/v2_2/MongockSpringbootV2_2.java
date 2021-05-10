package com.github.cloudyrock.springboot.v2_2;


import com.github.cloudyrock.springboot.base.SpringbootV2_2BuilderBase;
import com.github.cloudyrock.springboot.base.config.MongockSpringConfiguration;

public final class MongockSpringbootV2_2 {

  //TODO javadoc
  public static Builder builder() {
    return new Builder();
  }

  public static class Builder extends SpringbootV2_2BuilderBase<Builder, MongockSpringConfiguration> {


    //TODO javadoc
    @SuppressWarnings("unchecked")
    public MongockApplicationRunner buildApplicationRunner() {
      this.runner = getRunner();
      return args -> runner.execute();
    }


    //TODO javadoc
    @SuppressWarnings("unchecked")
    public MongockInitializingBeanRunner buildInitializingBeanRunner() {
      this.runner = getRunner();
      return () -> runner.execute();
    }

    @Override
    protected Builder getInstance() {
      return this;
    }
  }


  @FunctionalInterface
  public interface MongockApplicationRunner extends SpringbootV2_2BuilderBase.MongockApplicationRunnerBase {
  }

  @FunctionalInterface
  public interface MongockInitializingBeanRunner extends SpringbootV2_2BuilderBase.MongockInitializingBeanRunnerBase {
  }

}
