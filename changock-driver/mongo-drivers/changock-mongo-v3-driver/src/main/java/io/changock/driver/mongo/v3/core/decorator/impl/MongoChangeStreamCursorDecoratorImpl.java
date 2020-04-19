package io.changock.driver.mongo.v3.core.decorator.impl;

import com.mongodb.client.MongoChangeStreamCursor;
import io.changock.driver.core.lock.interceptor.decorator.MethodInvoker;
import io.changock.driver.mongo.v3.core.decorator.MongoChangeStreamCursorDecorator;

public class MongoChangeStreamCursorDecoratorImpl<T> implements MongoChangeStreamCursorDecorator<T> {

  private final MongoChangeStreamCursor<T> impl;
  private final MethodInvoker checker;

  public MongoChangeStreamCursorDecoratorImpl(MongoChangeStreamCursor<T> implementation, MethodInvoker lockerCheckInvoker) {
    this.impl = implementation;
    this.checker = lockerCheckInvoker;
  }

  @Override
  public MongoChangeStreamCursor<T> getImpl() {
    return impl;
  }

  @Override
  public MethodInvoker getInvoker() {
    return checker;
  }
}
