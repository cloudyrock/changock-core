package io.changock.driver.mongo.v3.core.decorator.impl;

import com.mongodb.client.MongoDatabase;
import io.changock.driver.api.lock.guard.invoker.LockGuardInvoker;
import io.changock.driver.mongo.v3.core.decorator.MongoDatabaseDecorator;

public class MongoDataBaseDecoratorImpl implements MongoDatabaseDecorator {
  private final MongoDatabase impl;
  private final LockGuardInvoker invoker;

  public MongoDataBaseDecoratorImpl(MongoDatabase implementation, LockGuardInvoker lockerCheckInvoker) {
    this.impl = implementation;
    this.invoker = lockerCheckInvoker;
  }

  public MongoDatabase getImpl() {
    return impl;
  }

  public LockGuardInvoker getInvoker() {
    return invoker;
  }

}
