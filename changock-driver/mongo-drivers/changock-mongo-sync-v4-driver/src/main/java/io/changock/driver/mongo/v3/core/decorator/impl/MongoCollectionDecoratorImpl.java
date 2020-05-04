package io.changock.driver.mongo.v3.core.decorator.impl;

import com.mongodb.client.MongoCollection;
import io.changock.driver.api.lock.guard.invoker.LockGuardInvoker;
import io.changock.driver.mongo.v3.core.decorator.MongoCollectionDecorator;

public class MongoCollectionDecoratorImpl<T> implements MongoCollectionDecorator<T> {

  private final MongoCollection<T> impl;
  private final LockGuardInvoker lockChecker;

  public MongoCollectionDecoratorImpl(MongoCollection<T> implementation, LockGuardInvoker lockerCheckInvoker) {
    this.impl = implementation;
    this.lockChecker = lockerCheckInvoker;
  }

  @Override
  public MongoCollection<T> getImpl() {
    return impl;
  }

  @Override
  public LockGuardInvoker getInvoker() {
    return lockChecker;
  }

}
