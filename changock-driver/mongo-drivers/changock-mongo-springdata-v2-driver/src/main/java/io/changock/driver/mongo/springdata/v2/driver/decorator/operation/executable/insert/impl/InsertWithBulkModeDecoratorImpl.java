package io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.insert.impl;

import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.insert.InsertWithBulkModeDecorator;
import io.changock.driver.mongo.springdata.v2.driver.decorator.util.DecoratorBase;
import io.changock.driver.core.lock.guard.invoker.MethodInvoker;
import org.springframework.data.mongodb.core.ExecutableInsertOperation;

public class InsertWithBulkModeDecoratorImpl<T>
    extends DecoratorBase<ExecutableInsertOperation.InsertWithBulkMode<T>>
    implements InsertWithBulkModeDecorator<T> {
  public InsertWithBulkModeDecoratorImpl(ExecutableInsertOperation.InsertWithBulkMode<T> impl, MethodInvoker invoker) {
    super(impl, invoker);
  }
}
