package com.github.cloudyrock.mongock.runner.core.builder.roles;

import com.github.cloudyrock.mongock.config.MongockConfiguration;
import com.github.cloudyrock.mongock.config.TransactionStrategy;

public interface TransactionStrategiable<SELF extends TransactionStrategiable<SELF, CONFIG>, CONFIG extends MongockConfiguration>
        extends Configurable<SELF, CONFIG>, SelfInstanstiator<SELF> {

   /**
   * Indicates if Mongock should run in transaction per ChangeLog or per Migration
   *
   * @param transactionStrategy if Mongock should run in transaction per ChangeLog or per Migration
   * @return builder for fluent interface
   */
    default SELF setTransactionStrategy(TransactionStrategy transactionStrategy) {
        getConfig().setTransactionStrategy(transactionStrategy);
        return getInstance();
    }
}
