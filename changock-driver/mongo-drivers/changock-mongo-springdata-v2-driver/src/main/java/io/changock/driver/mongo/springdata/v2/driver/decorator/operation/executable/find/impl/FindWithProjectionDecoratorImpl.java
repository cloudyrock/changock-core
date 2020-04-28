package io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.find.impl;

import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.find.FindWithProjectionDecorator;
import io.changock.driver.core.lock.guard.invoker.MethodInvoker;
import org.springframework.data.mongodb.core.ExecutableFindOperation;

public class FindWithProjectionDecoratorImpl<T> implements FindWithProjectionDecorator<T> {

  private final ExecutableFindOperation.FindWithProjection<T> impl;

  private final MethodInvoker invoker;

  public FindWithProjectionDecoratorImpl(ExecutableFindOperation.FindWithProjection<T> impl, MethodInvoker invoker) {
    this.impl = impl;
    this.invoker = invoker;
  }

  @Override
  public ExecutableFindOperation.FindWithProjection<T> getImpl() {
    return impl;
  }

  @Override
  public MethodInvoker getInvoker() {
    return invoker;
  }
}
