package com.github.cloudyrock.standalone;

import com.github.cloudyrock.mongock.config.MongockConfiguration;
import com.github.cloudyrock.mongock.runner.core.executor.ExecutorFactory;
import com.github.cloudyrock.mongock.runner.core.executor.migration.ExecutorConfiguration;

public final class MongockStandalone {

  //TODO javadoc
  public static Builder builder() {
    return new Builder(new ExecutorFactory<>());
  }

  public static class Builder extends StandaloneBuilderBase<Builder, MongockConfiguration, ExecutorConfiguration> {
    protected Builder(ExecutorFactory<ExecutorConfiguration> executorFactory) {
      super(executorFactory);
    }

    @Override
    protected ExecutorConfiguration getExecutorConfiguration() {
      return new ExecutorConfiguration(trackIgnored, serviceIdentifier);
    }

    @Override
    protected Builder getInstance() {
      return this;
    }

  }

}
