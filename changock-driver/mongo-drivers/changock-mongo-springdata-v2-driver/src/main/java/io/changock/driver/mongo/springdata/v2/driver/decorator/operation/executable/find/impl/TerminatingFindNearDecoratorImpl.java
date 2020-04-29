package io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.find.impl;

import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.find.TerminatingFindNearDecorator;
import io.changock.driver.core.lock.guard.invoker.LockGuardInvoker;
import org.springframework.data.mongodb.core.ExecutableFindOperation;

public class TerminatingFindNearDecoratorImpl<T> implements TerminatingFindNearDecorator<T> {

  private final ExecutableFindOperation.TerminatingFindNear<T> impl;

  private final LockGuardInvoker invoker;

  public TerminatingFindNearDecoratorImpl(ExecutableFindOperation.TerminatingFindNear<T> impl, LockGuardInvoker invoker) {
    this.impl = impl;
    this.invoker = invoker;
  }

  @Override
  public ExecutableFindOperation.TerminatingFindNear<T> getImpl() {
    return impl;
  }

  @Override
  public LockGuardInvoker getInvoker() {
    return invoker;
  }
}
