package com.github.cloudyrock.mongock.runner.core.builder.roles;

import com.github.cloudyrock.mongock.runner.core.executor.MongockRunner;

public interface RunnerBuilder<SELF extends RunnerBuilder<SELF, R>, R> {
  /**
   * Feature which enables/disables execution
   * <b>Optional</b> Default value true.
   *
   * @param enabled Migration process will run only if this option is set to true
   * @return builder for fluent interface
   */
  SELF setEnabled(boolean enabled);

  //todo javadoc
  MongockRunner<R> buildRunner();
}
