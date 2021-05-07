package com.github.cloudyrock.mongock;


public enum TransactionStrategy {
  NONE, MIGRATION, CHANGE_LOG;

  public boolean isTransactionable() {
    return this != NONE;
  }
}
