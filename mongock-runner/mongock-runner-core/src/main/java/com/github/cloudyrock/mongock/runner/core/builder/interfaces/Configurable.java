package com.github.cloudyrock.mongock.runner.core.builder.interfaces;

import com.github.cloudyrock.mongock.config.MongockConfiguration;

public interface Configurable<BUILDER_TYPE extends Configurable, CONFIG extends MongockConfiguration> {
  //TODO javadoc
  BUILDER_TYPE setConfig(CONFIG config);
}
