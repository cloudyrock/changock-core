package io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.find.impl;

import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.find.FindWithQueryDecorator;
import io.changock.driver.core.lock.guard.invoker.MethodInvoker;
import org.springframework.data.mongodb.core.ExecutableFindOperation;

public class FindWithQueryDecoratorImpl<T> implements FindWithQueryDecorator<T> {

  private final ExecutableFindOperation.FindWithQuery<T> impl;
  private final MethodInvoker invoker;

  public FindWithQueryDecoratorImpl(ExecutableFindOperation.FindWithQuery<T> impl, MethodInvoker invoker) {
    this.impl = impl;
    this.invoker = invoker;
  }

  @Override
  public ExecutableFindOperation.FindWithQuery<T> getImpl() {
    return impl;
  }

  @Override
  public MethodInvoker getInvoker() {
    return invoker;
  }
}
