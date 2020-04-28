package io.changock.driver.mongo.springdata.v2.driver.decorator.util;

import io.changock.driver.core.lock.guard.invoker.MethodInvoker;

public abstract class DecoratorBase<T> implements Invokable {

  private final MethodInvoker invoker;
  private final T impl;

  public DecoratorBase(T impl, MethodInvoker invoker) {
    this.invoker = invoker;
    this.impl = impl;
  }

  public MethodInvoker getInvoker() {
    return invoker;
  }

  public T getImpl() {
    return impl;
  }
}
