package io.changock.driver.mongo.springdata.v2.driver.decorator.util;

import io.changock.driver.core.lock.guard.invoker.MethodInvoker;

public interface Invokable {
  MethodInvoker getInvoker();
}
