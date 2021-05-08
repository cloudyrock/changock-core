package com.github.cloudyrock.mongock.driver.api.driver;

import com.github.cloudyrock.mongock.TransactionStrategy;

public interface Transactionable {

  //return transactionResult: OK, Failed(exception, etc)??
  void executeInTransaction(Runnable operation);

  /**
   * Retrieve the transaction strategy
   * @see TransactionStrategy
   * @return transactionStrategy
   */
  TransactionStrategy getTransactionStrategy();

  /**
   * Mechanism to disabled transactions in case they are available.
   */
  void disableTransaction();
}
