package io.changock.driver.mongo.v3.core.decorator.impl;

import com.mongodb.client.AggregateIterable;
import io.changock.driver.core.decorator.MethodInvoker;
import io.changock.driver.mongo.v3.core.decorator.AggregateIterableDecorator;

public class AggregateIterableDecoratorImpl<T> implements AggregateIterableDecorator<T> {

  private final AggregateIterable<T> impl;
  private final MethodInvoker checker;

  public AggregateIterableDecoratorImpl(AggregateIterable<T> implementation, MethodInvoker lockerCheckInvoker) {
    this.impl = implementation;
    this.checker = lockerCheckInvoker;
  }

  @Override
  public AggregateIterable<T> getImpl() {
    return impl;
  }

  @Override
  public MethodInvoker getInvoker() {
    return checker;
  }

}
