package com.github.cloudyrock.mongock.driver.api.driver;


public enum TransactionStrategy {
  NONE, MIGRATION, CHANGE_SET, CHANGE_LOG;

  public boolean isTransactionable() {
    return this != NONE;
  }
}
