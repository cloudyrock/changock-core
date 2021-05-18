package com.github.cloudyrock.standalone;

import com.github.cloudyrock.mongock.config.MongockConfiguration;
import com.github.cloudyrock.mongock.runner.core.executor.ExecutorFactory;

public final class MongockStandalone {

  //TODO javadoc
  public static Builder builder() {
    return new Builder(new ExecutorFactory(), new MongockConfiguration());
  }

  public static class Builder extends StandaloneBuilderBase<Builder, MongockConfiguration> {
    private Builder(ExecutorFactory executorFactory, MongockConfiguration config) {
      super(executorFactory, config);
    }


    @Override
    protected Builder getInstance() {
      return this;
    }


  }

}
