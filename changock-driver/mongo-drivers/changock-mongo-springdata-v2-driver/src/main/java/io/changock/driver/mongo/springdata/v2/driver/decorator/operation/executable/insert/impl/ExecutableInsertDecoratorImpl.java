package io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.insert.impl;

import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.insert.ExecutableInsertDecorator;
import io.changock.driver.mongo.springdata.v2.driver.decorator.util.DecoratorBase;
import io.changock.driver.core.lock.guard.invoker.MethodInvoker;
import org.springframework.data.mongodb.core.ExecutableInsertOperation;

public class ExecutableInsertDecoratorImpl<T>
    extends DecoratorBase<ExecutableInsertOperation.ExecutableInsert<T>>
    implements ExecutableInsertDecorator<T> {
  public ExecutableInsertDecoratorImpl(ExecutableInsertOperation.ExecutableInsert<T> impl, MethodInvoker invoker) {
    super(impl, invoker);
  }
}
