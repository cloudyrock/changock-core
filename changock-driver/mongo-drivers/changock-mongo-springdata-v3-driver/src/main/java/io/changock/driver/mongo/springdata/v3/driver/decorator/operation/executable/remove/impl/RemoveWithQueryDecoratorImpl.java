package io.changock.driver.mongo.springdata.v3.driver.decorator.operation.executable.remove.impl;

import io.changock.driver.mongo.springdata.v3.driver.decorator.operation.executable.remove.RemoveWithQueryDecorator;
import io.changock.driver.api.lock.guard.decorator.DecoratorBase;
import io.changock.driver.api.lock.guard.invoker.LockGuardInvoker;
import org.springframework.data.mongodb.core.ExecutableRemoveOperation;

public class RemoveWithQueryDecoratorImpl<T> extends DecoratorBase<ExecutableRemoveOperation.RemoveWithQuery<T>> implements RemoveWithQueryDecorator<T> {
  public RemoveWithQueryDecoratorImpl(ExecutableRemoveOperation.RemoveWithQuery<T> impl, LockGuardInvoker invoker) {
    super(impl, invoker);
  }
}
