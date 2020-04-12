package io.changock.driver.mongo.v3.core.decorator.impl;

import com.mongodb.client.MapReduceIterable;
import io.changock.driver.core.decorator.MethodInvoker;
import io.changock.driver.mongo.v3.core.decorator.MapReduceIterableDecorator;

public class MapReduceIterableDecoratorImpl<T> implements MapReduceIterableDecorator<T> {

  private final MapReduceIterable<T> impl;
  private final MethodInvoker checker;

  public MapReduceIterableDecoratorImpl(MapReduceIterable<T> implementation, MethodInvoker lockerCheckInvoker) {
    this.impl = implementation;
    this.checker = lockerCheckInvoker;
  }

  @Override
  public MapReduceIterable<T> getImpl() {
    return impl;
  }

  @Override
  public MethodInvoker getInvoker() {
    return checker;
  }
}
