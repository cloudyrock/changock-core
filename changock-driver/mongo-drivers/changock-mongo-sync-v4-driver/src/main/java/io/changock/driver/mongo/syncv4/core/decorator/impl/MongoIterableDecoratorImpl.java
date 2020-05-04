package io.changock.driver.mongo.syncv4.core.decorator.impl;

import com.mongodb.client.MongoIterable;
import io.changock.driver.api.lock.guard.invoker.LockGuardInvoker;
import io.changock.driver.mongo.syncv4.core.decorator.MongoIterableDecorator;

public class MongoIterableDecoratorImpl<T> implements MongoIterableDecorator<T> {

  private final MongoIterable<T> impl;
  private final LockGuardInvoker checker;

  public MongoIterableDecoratorImpl(MongoIterable<T> implementation, LockGuardInvoker lockerCheckInvoker) {
    this.impl = implementation;
    this.checker = lockerCheckInvoker;
  }

  @Override
  public MongoIterable<T> getImpl() {
    return impl;
  }

  @Override
  public LockGuardInvoker getInvoker() {
    return checker;
  }
}
