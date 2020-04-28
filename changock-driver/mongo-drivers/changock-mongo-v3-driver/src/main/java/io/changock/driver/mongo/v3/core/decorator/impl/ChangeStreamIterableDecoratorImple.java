package io.changock.driver.mongo.v3.core.decorator.impl;

import com.mongodb.client.ChangeStreamIterable;
import io.changock.driver.core.lock.guard.invoker.LockGuardInvoker;
import io.changock.driver.mongo.v3.core.decorator.ChangeStreamIterableDecorator;

public class ChangeStreamIterableDecoratorImple<T> implements ChangeStreamIterableDecorator<T> {

  private final ChangeStreamIterable<T> impl;
  private final LockGuardInvoker checker;

  public ChangeStreamIterableDecoratorImple(ChangeStreamIterable<T> implementation, LockGuardInvoker lockerCheckInvoker) {
    this.impl = implementation;
    this.checker = lockerCheckInvoker;
  }

  @Override
  public ChangeStreamIterable<T> getImpl() {
    return impl;
  }

  @Override
  public LockGuardInvoker getInvoker() {
    return checker;
  }
}
