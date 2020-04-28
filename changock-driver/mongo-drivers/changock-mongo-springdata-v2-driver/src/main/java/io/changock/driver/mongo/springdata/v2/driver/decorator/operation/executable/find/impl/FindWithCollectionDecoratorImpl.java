package io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.find.impl;

import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.find.FindWithCollectionDecorator;
import io.changock.driver.core.lock.guard.invoker.MethodInvoker;
import org.springframework.data.mongodb.core.ExecutableFindOperation;

public class FindWithCollectionDecoratorImpl<T> implements FindWithCollectionDecorator<T> {

  private final ExecutableFindOperation.FindWithCollection<T> impl;

  private final MethodInvoker invoker;

  public FindWithCollectionDecoratorImpl(ExecutableFindOperation.FindWithCollection<T> impl, MethodInvoker invoker) {
    this.impl = impl;
    this.invoker = invoker;
  }

  @Override
  public ExecutableFindOperation.FindWithCollection<T> getImpl() {
    return impl;
  }

  @Override
  public MethodInvoker getInvoker() {
    return invoker;
  }
}
