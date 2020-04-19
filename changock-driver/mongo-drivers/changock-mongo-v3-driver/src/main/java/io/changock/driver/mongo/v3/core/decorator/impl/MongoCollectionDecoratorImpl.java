package io.changock.driver.mongo.v3.core.decorator.impl;

import com.mongodb.client.MongoCollection;
import io.changock.driver.core.lock.interceptor.decorator.MethodInvoker;
import io.changock.driver.mongo.v3.core.decorator.MongoCollectionDecorator;

public class MongoCollectionDecoratorImpl<T> implements MongoCollectionDecorator<T> {

  private final MongoCollection<T> impl;
  private final MethodInvoker lockChecker;

  public MongoCollectionDecoratorImpl(MongoCollection<T> implementation, MethodInvoker lockerCheckInvoker) {
    this.impl = implementation;
    this.lockChecker = lockerCheckInvoker;
  }

  @Override
  public MongoCollection<T> getImpl() {
    return impl;
  }

  @Override
  public MethodInvoker getInvoker() {
    return lockChecker;
  }

}
