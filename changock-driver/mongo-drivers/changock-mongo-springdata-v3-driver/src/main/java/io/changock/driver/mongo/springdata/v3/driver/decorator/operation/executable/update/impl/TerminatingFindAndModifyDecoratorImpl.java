package io.changock.driver.mongo.springdata.v3.driver.decorator.operation.executable.update.impl;

import io.changock.driver.mongo.springdata.v3.driver.decorator.operation.executable.update.TerminatingFindAndModifyDecorator;
import io.changock.driver.api.lock.guard.decorator.DecoratorBase;
import io.changock.driver.api.lock.guard.invoker.LockGuardInvoker;
import org.springframework.data.mongodb.core.ExecutableUpdateOperation;

public class TerminatingFindAndModifyDecoratorImpl<T> extends DecoratorBase<ExecutableUpdateOperation.TerminatingFindAndModify<T>> implements TerminatingFindAndModifyDecorator<T> {

  public TerminatingFindAndModifyDecoratorImpl(ExecutableUpdateOperation.TerminatingFindAndModify<T> impl, LockGuardInvoker invoker) {
    super(impl, invoker);
  }

}
