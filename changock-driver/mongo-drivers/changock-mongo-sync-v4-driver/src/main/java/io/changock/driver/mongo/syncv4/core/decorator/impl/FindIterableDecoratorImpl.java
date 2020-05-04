package io.changock.driver.mongo.syncv4.core.decorator.impl;

import com.mongodb.client.FindIterable;
import io.changock.driver.api.lock.guard.invoker.LockGuardInvoker;
import io.changock.driver.mongo.syncv4.core.decorator.FindIterableDecorator;

public class FindIterableDecoratorImpl<T> implements FindIterableDecorator<T> {

  private final FindIterable<T> impl;
  private final LockGuardInvoker checker;

  public FindIterableDecoratorImpl(FindIterable<T> implementation, LockGuardInvoker lockerCheckInvoker) {
    this.impl = implementation;
    this.checker = lockerCheckInvoker;
  }

  @Override
  public FindIterable<T> getImpl() {
    return impl;
  }

  @Override
  public LockGuardInvoker getInvoker() {
    return checker;
  }
}
