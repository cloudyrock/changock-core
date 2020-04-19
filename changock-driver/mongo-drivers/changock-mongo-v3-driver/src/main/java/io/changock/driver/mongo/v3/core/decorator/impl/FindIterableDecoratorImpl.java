package io.changock.driver.mongo.v3.core.decorator.impl;

import com.mongodb.client.FindIterable;
import io.changock.driver.core.interceptor.decorator.MethodInvoker;
import io.changock.driver.mongo.v3.core.decorator.FindIterableDecorator;

public class FindIterableDecoratorImpl<T> implements FindIterableDecorator<T> {

  private final FindIterable<T> impl;
  private final MethodInvoker checker;

  public FindIterableDecoratorImpl(FindIterable<T> implementation, MethodInvoker lockerCheckInvoker) {
    this.impl = implementation;
    this.checker = lockerCheckInvoker;
  }

  @Override
  public FindIterable<T> getImpl() {
    return impl;
  }

  @Override
  public MethodInvoker getInvoker() {
    return checker;
  }
}
