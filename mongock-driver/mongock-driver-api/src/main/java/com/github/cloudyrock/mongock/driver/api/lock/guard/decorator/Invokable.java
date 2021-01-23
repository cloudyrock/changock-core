package com.github.cloudyrock.mongock.driver.api.lock.guard.decorator;

import com.github.cloudyrock.mongock.driver.api.lock.guard.invoker.LockGuardInvoker;

public interface Invokable {
  LockGuardInvoker getInvoker();
}
