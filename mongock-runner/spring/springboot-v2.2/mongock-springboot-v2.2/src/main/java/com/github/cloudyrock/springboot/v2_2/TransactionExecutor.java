package com.github.cloudyrock.springboot.v2_2;

import java.lang.reflect.InvocationTargetException;

public interface TransactionExecutor {
  void executionInTransaction(Runnable operation) throws IllegalAccessException, InvocationTargetException;
}
