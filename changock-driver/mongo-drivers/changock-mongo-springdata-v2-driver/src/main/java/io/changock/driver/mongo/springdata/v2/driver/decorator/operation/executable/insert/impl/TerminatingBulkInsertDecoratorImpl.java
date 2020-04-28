package io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.insert.impl;

import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.insert.TerminatingBulkInsertDecorator;
import io.changock.driver.mongo.springdata.v2.driver.decorator.util.DecoratorBase;
import io.changock.driver.core.lock.guard.invoker.MethodInvoker;
import org.springframework.data.mongodb.core.ExecutableInsertOperation;

public class TerminatingBulkInsertDecoratorImpl<T>
    extends DecoratorBase<ExecutableInsertOperation.TerminatingBulkInsert<T>>
    implements TerminatingBulkInsertDecorator<T> {
  public TerminatingBulkInsertDecoratorImpl(ExecutableInsertOperation.TerminatingBulkInsert<T> impl, MethodInvoker invoker) {
    super(impl, invoker);
  }
}
