package com.github.cloudyrock.mongock.runner.core.builder.interfaces;

import com.github.cloudyrock.mongock.runner.core.executor.MongockRunner;

public interface RunnerBuilder<BUILDER_TYPE extends RunnerBuilder, RETURN_TYPE> {
  /**
   * Feature which enables/disables execution
   * <b>Optional</b> Default value true.
   *
   * @param enabled Migration process will run only if this option is set to true
   * @return builder for fluent interface
   */
  BUILDER_TYPE setEnabled(boolean enabled);

  //todo javadoc
  MongockRunner<RETURN_TYPE> buildRunner();
}
