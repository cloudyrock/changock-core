package io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.find.impl;

import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.find.TerminatingFindDecorator;
import io.changock.driver.core.lock.guard.invoker.MethodInvoker;
import org.springframework.data.mongodb.core.ExecutableFindOperation;

public class TerminatingFindDecoratorImpl<T> implements TerminatingFindDecorator<T> {

  private final ExecutableFindOperation.TerminatingFind<T> impl;
  private final MethodInvoker invoker;

  public TerminatingFindDecoratorImpl(ExecutableFindOperation.TerminatingFind<T> impl, MethodInvoker invoker) {
    this.impl = impl;
    this.invoker = invoker;
  }

  @Override
  public ExecutableFindOperation.TerminatingFind<T> getImpl() {
    return impl;
  }

  @Override
  public MethodInvoker getInvoker() {
    return invoker;
  }
}
