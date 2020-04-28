package io.changock.driver.mongo.v3.core.decorator.impl;

import com.mongodb.client.DistinctIterable;
import io.changock.driver.core.lock.guard.invoker.LockGuardInvoker;
import io.changock.driver.mongo.v3.core.decorator.DistinctIterableDecorator;

public class DistinctIterableDecoratorImpl<T> implements DistinctIterableDecorator<T> {

  private final DistinctIterable<T> impl;
  private final LockGuardInvoker checker;

  public DistinctIterableDecoratorImpl(DistinctIterable<T> implementation, LockGuardInvoker lockerCheckInvoker) {
    this.impl = implementation;
    this.checker = lockerCheckInvoker;
  }

  @Override
  public DistinctIterable<T> getImpl() {
    return impl;
  }

  @Override
  public LockGuardInvoker getInvoker() {
    return checker;
  }
}
