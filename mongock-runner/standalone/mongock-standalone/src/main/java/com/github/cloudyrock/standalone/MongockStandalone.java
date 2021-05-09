package com.github.cloudyrock.standalone;

import com.github.cloudyrock.mongock.config.MongockConfiguration;

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
