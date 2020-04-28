package io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.find.impl;

import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.find.FindDistinctDecorator;
import io.changock.driver.core.lock.guard.invoker.MethodInvoker;
import org.springframework.data.mongodb.core.ExecutableFindOperation;

public class FindDistinctDecoratorImpl implements FindDistinctDecorator {

  private final ExecutableFindOperation.FindDistinct impl;

  private final MethodInvoker invoker;

  public FindDistinctDecoratorImpl(ExecutableFindOperation.FindDistinct impl, MethodInvoker invoker) {
    this.impl = impl;
    this.invoker = invoker;
  }

  @Override
  public ExecutableFindOperation.FindDistinct getImpl() {
    return impl;
  }

  @Override
  public MethodInvoker getInvoker() {
    return invoker;
  }
}
