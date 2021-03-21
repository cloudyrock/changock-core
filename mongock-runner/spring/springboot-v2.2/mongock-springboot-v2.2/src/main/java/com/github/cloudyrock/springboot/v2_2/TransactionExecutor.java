package com.github.cloudyrock.springboot.v2_2;

public interface TransactionExecutor {
  void executionInTransaction(Runnable operation);
}
