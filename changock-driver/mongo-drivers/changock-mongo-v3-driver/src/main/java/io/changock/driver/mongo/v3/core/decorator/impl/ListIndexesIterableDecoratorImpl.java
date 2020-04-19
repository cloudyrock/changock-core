package io.changock.driver.mongo.v3.core.decorator.impl;

import com.mongodb.client.ListIndexesIterable;
import io.changock.driver.core.lock.interceptor.decorator.MethodInvoker;
import io.changock.driver.mongo.v3.core.decorator.ListIndexesIterableDecorator;

public class ListIndexesIterableDecoratorImpl<T> implements ListIndexesIterableDecorator<T> {

  private final ListIndexesIterable<T> impl;
  private final MethodInvoker checker;

  public ListIndexesIterableDecoratorImpl(ListIndexesIterable<T> implementation, MethodInvoker lockerCheckInvoker) {
    this.impl = implementation;
    this.checker = lockerCheckInvoker;
  }

  @Override
  public ListIndexesIterable<T> getImpl() {
    return impl;
  }

  @Override
  public MethodInvoker getInvoker() {
    return checker;
  }

}
