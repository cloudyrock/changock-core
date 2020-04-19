package io.changock.driver.mongo.v3.core.decorator.impl;

import com.mongodb.client.ChangeStreamIterable;
import io.changock.driver.core.lock.guard.invoker.MethodInvoker;
import io.changock.driver.mongo.v3.core.decorator.ChangeStreamIterableDecorator;

public class ChangeStreamIterableDecoratorImple<T> implements ChangeStreamIterableDecorator<T> {

  private final ChangeStreamIterable<T> impl;
  private final MethodInvoker checker;

  public ChangeStreamIterableDecoratorImple(ChangeStreamIterable<T> implementation, MethodInvoker lockerCheckInvoker) {
    this.impl = implementation;
    this.checker = lockerCheckInvoker;
  }

  @Override
  public ChangeStreamIterable<T> getImpl() {
    return impl;
  }

  @Override
  public MethodInvoker getInvoker() {
    return checker;
  }
}
