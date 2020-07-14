package io.changock.driver.api.driver;

public interface Transactionable {

  //return transactionResult: OK, Failed(exception, etc)??
  void executeInTransaction(Runnable operation);

  TransactionStrategy getTransactionStrategy();
}
