package com.github.cloudyrock.standalone;

import com.github.cloudyrock.mongock.config.MongockConfiguration;
import com.github.cloudyrock.mongock.exception.MongockException;
import com.github.cloudyrock.mongock.runner.core.builder.RunnerBuilderBase;
import com.github.cloudyrock.standalone.event.StandaloneEventPublisher;
import com.github.cloudyrock.standalone.event.StandaloneMigrationFailureEvent;
import com.github.cloudyrock.standalone.event.StandaloneMigrationSuccessEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public final class MongockStandalone {

  //TODO javadoc
  public static Builder builder() {
    return new Builder();
  }

  public static class Builder extends StandaloneBuilderBase<Builder, MongockConfiguration> {
    @Override
    protected Builder getInstance() {
      return this;
    }

  }

}
