package io.changock.driver.mongo.v3.core.decorator.impl;

import com.mongodb.client.ListCollectionsIterable;
import io.changock.driver.core.decorator.MethodInvoker;
import io.changock.driver.mongo.v3.core.decorator.ListCollectionsIterableDecorator;

public class ListCollectionsIterableDecoratorImpl<T> implements ListCollectionsIterableDecorator<T> {

  private final ListCollectionsIterable<T> impl;
  private final MethodInvoker checker;

  public ListCollectionsIterableDecoratorImpl(ListCollectionsIterable<T> implementation, MethodInvoker lockerCheckInvoker) {
    this.impl = implementation;
    this.checker = lockerCheckInvoker;
  }

  @Override
  public ListCollectionsIterable<T> getImpl() {
    return impl;
  }

  @Override
  public MethodInvoker getInvoker() {
    return checker;
  }

}
