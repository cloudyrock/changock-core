package io.changock.driver.mongo.springdata.v3.driver.decorator.operation.executable.insert.impl;

import io.changock.driver.mongo.springdata.v3.driver.decorator.operation.executable.insert.ExecutableInsertDecorator;
import io.changock.driver.api.lock.guard.decorator.DecoratorBase;
import io.changock.driver.api.lock.guard.invoker.LockGuardInvoker;
import org.springframework.data.mongodb.core.ExecutableInsertOperation;

public class ExecutableInsertDecoratorImpl<T>
    extends DecoratorBase<ExecutableInsertOperation.ExecutableInsert<T>>
    implements ExecutableInsertDecorator<T> {
  public ExecutableInsertDecoratorImpl(ExecutableInsertOperation.ExecutableInsert<T> impl, LockGuardInvoker invoker) {
    super(impl, invoker);
  }
}
