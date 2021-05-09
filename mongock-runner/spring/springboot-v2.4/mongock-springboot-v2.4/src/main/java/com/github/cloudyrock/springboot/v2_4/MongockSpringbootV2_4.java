package com.github.cloudyrock.springboot.v2_4;


public final class MongockSpringbootV2_4 {


  //TODO javadoc
  public static Builder builder() {
    return new Builder();
  }

  public static class Builder extends SpringbootV2_4BuilderBase<Builder> {


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
  public interface MongockApplicationRunner extends SpringbootV2_4BuilderBase.MongockApplicationRunnerBase {
  }

  @FunctionalInterface
  public interface MongockInitializingBeanRunner extends SpringbootV2_4BuilderBase.MongockInitializingBeanRunnerBase {
  }

}
