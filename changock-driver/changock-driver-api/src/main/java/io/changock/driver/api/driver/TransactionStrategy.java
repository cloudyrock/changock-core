package io.changock.driver.api.driver;


public enum TransactionStrategy {
  NONE
  ,ENTIRE_MIGRATION
  ,CHANGE_SET
  ,CHANGE_LOG;

  public boolean isTransactionable() {
    return this != NONE;
  }
}
