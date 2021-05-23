package com.github.cloudyrock.mongock.runner.core.builder.roles;

import com.github.cloudyrock.mongock.config.MongockConfiguration;

public interface Configurable<BUILDER_TYPE extends Configurable, CONFIG extends MongockConfiguration> {
  //TODO javadoc
  BUILDER_TYPE setConfig(CONFIG config);
}
