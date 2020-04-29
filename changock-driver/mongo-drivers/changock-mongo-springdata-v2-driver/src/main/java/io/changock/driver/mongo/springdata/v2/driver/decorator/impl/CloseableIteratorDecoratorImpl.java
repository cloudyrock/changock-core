package io.changock.driver.mongo.springdata.v2.driver.decorator.impl;

import io.changock.driver.mongo.springdata.v2.driver.decorator.CloseableIteratorDecorator;
import io.changock.driver.core.lock.guard.invoker.LockGuardInvoker;
import org.springframework.data.util.CloseableIterator;

public class CloseableIteratorDecoratorImpl<T> implements CloseableIteratorDecorator<T> {

  private final LockGuardInvoker invoker;
  private final CloseableIterator<T> impl;

  public CloseableIteratorDecoratorImpl(CloseableIterator<T> implementation, LockGuardInvoker invoker) {
    this.impl = implementation;
    this.invoker = invoker;
  }

  @Override
  public CloseableIterator<T> getImpl() {
    return impl;
  }

  @Override
  public LockGuardInvoker getInvoker() {
    return invoker;
  }


}
