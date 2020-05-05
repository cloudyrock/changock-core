package io.changock.driver.mongo.springdata.v3.driver.decorator.operation.executable.update.impl;

import io.changock.driver.mongo.springdata.v3.driver.decorator.operation.executable.update.ExecutableUpdateDecorator;
import io.changock.driver.api.lock.guard.decorator.DecoratorBase;
import io.changock.driver.api.lock.guard.invoker.LockGuardInvoker;
import org.springframework.data.mongodb.core.ExecutableUpdateOperation;

public class ExecutableUpdateDecoratorImpl<T> extends DecoratorBase<ExecutableUpdateOperation.ExecutableUpdate<T>> implements ExecutableUpdateDecorator<T> {

  public ExecutableUpdateDecoratorImpl(ExecutableUpdateOperation.ExecutableUpdate<T> impl, LockGuardInvoker invoker) {
    super(impl, invoker);
  }

}
