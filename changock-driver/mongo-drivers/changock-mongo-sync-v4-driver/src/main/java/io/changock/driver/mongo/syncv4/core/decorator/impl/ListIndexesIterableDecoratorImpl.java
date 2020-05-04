package io.changock.driver.mongo.syncv4.core.decorator.impl;

import com.mongodb.client.ListIndexesIterable;
import io.changock.driver.api.lock.guard.invoker.LockGuardInvoker;
import io.changock.driver.mongo.syncv4.core.decorator.ListIndexesIterableDecorator;

public class ListIndexesIterableDecoratorImpl<T> implements ListIndexesIterableDecorator<T> {

  private final ListIndexesIterable<T> impl;
  private final LockGuardInvoker checker;

  public ListIndexesIterableDecoratorImpl(ListIndexesIterable<T> implementation, LockGuardInvoker lockerCheckInvoker) {
    this.impl = implementation;
    this.checker = lockerCheckInvoker;
  }

  @Override
  public ListIndexesIterable<T> getImpl() {
    return impl;
  }

  @Override
  public LockGuardInvoker getInvoker() {
    return checker;
  }

}
