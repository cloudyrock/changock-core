package io.changock.driver.api.lock.guard.decorator;

import io.changock.driver.api.lock.guard.invoker.LockGuardInvoker;

public interface Invokable {
  LockGuardInvoker getInvoker();
}
