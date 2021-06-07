package com.github.cloudyrock.mongock.runner.core.builder.roles;

import com.github.cloudyrock.mongock.config.MongockConfiguration;

public interface Configurable<SELF extends Configurable<SELF, CONFIG>, CONFIG extends MongockConfiguration> {
  CONFIG getConfig();

  //TODO javadoc
  SELF setConfig(CONFIG config);
}
