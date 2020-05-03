package io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.update.impl;

import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.update.FindAndModifyWithOptionsDecorator;
import io.changock.driver.api.lock.guard.decorator.DecoratorBase;
import io.changock.driver.api.lock.guard.invoker.LockGuardInvoker;
import org.springframework.data.mongodb.core.ExecutableUpdateOperation;

public class FindAndModifyWithOptionsDecoratorImpl<T> extends DecoratorBase<ExecutableUpdateOperation.FindAndModifyWithOptions<T>> implements FindAndModifyWithOptionsDecorator<T> {

  public FindAndModifyWithOptionsDecoratorImpl(ExecutableUpdateOperation.FindAndModifyWithOptions<T> impl, LockGuardInvoker invoker) {
    super(impl, invoker);
  }

}
