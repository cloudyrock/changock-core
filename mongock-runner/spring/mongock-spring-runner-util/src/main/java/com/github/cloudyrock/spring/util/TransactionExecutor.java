package com.github.cloudyrock.spring.util;

public interface TransactionExecutor {
  void executionInTransaction(Runnable operation);
}
