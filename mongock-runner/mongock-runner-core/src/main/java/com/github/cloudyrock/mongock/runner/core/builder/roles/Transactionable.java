package com.github.cloudyrock.mongock.runner.core.builder.roles;

import com.github.cloudyrock.mongock.config.MongockConfiguration;

public interface Transactionable<SELF extends Transactionable<SELF, CONFIG>, CONFIG extends MongockConfiguration>
    extends Configurable<SELF, CONFIG>, SelfInstanstiator<SELF> {
  /**
   * Indicates if Mongock should run in transaction mode or not
   *
   * @param transactionEnabled if Mongock should run in transaction mode
   * @return builder for fluent interface
   */
  default SELF setTransactionEnabled(boolean transactionEnabled) {
    getConfig().setTransactionEnabled(transactionEnabled);
    return getInstance();
  }
}
