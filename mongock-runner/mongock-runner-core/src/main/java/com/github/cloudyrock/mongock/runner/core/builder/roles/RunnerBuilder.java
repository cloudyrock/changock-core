package com.github.cloudyrock.mongock.runner.core.builder.roles;

import com.github.cloudyrock.mongock.config.MongockConfiguration;
import com.github.cloudyrock.mongock.runner.core.executor.MongockRunner;

public interface RunnerBuilder<SELF extends RunnerBuilder<SELF, R, CONFIG>, R, CONFIG extends MongockConfiguration>
    extends Configurable<SELF, CONFIG>, SelfInstanstiator<SELF> {

  SELF setExecutionId(String executionId);

  /**
   * Feature which enables/disables execution
   * <b>Optional</b> Default value true.
   *
   * @param enabled Migration process will run only if this option is set to true
   * @return builder for fluent interface
   */
  default SELF setEnabled(boolean enabled) {
    getConfig().setEnabled(enabled);
    return getInstance();
  }

  //todo javadoc
  MongockRunner<R> buildRunner();
}
