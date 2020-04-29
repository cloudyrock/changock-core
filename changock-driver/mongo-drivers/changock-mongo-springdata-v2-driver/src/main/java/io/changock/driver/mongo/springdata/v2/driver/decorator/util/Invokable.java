package io.changock.driver.mongo.springdata.v2.driver.decorator.util;

import io.changock.driver.api.lock.guard.invoker.LockGuardInvoker;

public interface Invokable {
  LockGuardInvoker getInvoker();
}
