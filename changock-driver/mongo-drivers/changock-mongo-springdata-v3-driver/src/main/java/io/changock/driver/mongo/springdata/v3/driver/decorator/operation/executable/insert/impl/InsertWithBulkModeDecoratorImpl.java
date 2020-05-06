package io.changock.driver.mongo.springdata.v3.driver.decorator.operation.executable.insert.impl;

import io.changock.driver.mongo.springdata.v3.driver.decorator.operation.executable.insert.InsertWithBulkModeDecorator;
import io.changock.driver.api.lock.guard.decorator.DecoratorBase;
import io.changock.driver.api.lock.guard.invoker.LockGuardInvoker;
import org.springframework.data.mongodb.core.ExecutableInsertOperation;

public class InsertWithBulkModeDecoratorImpl<T>
    extends DecoratorBase<ExecutableInsertOperation.InsertWithBulkMode<T>>
    implements InsertWithBulkModeDecorator<T> {
  public InsertWithBulkModeDecoratorImpl(ExecutableInsertOperation.InsertWithBulkMode<T> impl, LockGuardInvoker invoker) {
    super(impl, invoker);
  }
}