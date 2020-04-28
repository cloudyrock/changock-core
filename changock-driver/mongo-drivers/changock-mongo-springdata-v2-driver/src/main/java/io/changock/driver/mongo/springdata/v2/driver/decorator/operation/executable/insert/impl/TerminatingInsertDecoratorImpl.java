package io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.insert.impl;

import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.insert.TerminatingInsertDecorator;
import io.changock.driver.mongo.springdata.v2.driver.decorator.util.DecoratorBase;
import io.changock.driver.core.lock.guard.invoker.MethodInvoker;
import org.springframework.data.mongodb.core.ExecutableInsertOperation;

public class TerminatingInsertDecoratorImpl<T>
    extends DecoratorBase<ExecutableInsertOperation.TerminatingInsert<T>>
    implements TerminatingInsertDecorator<T> {
  public TerminatingInsertDecoratorImpl(ExecutableInsertOperation.TerminatingInsert<T> impl, MethodInvoker invoker) {
    super(impl, invoker);
  }
}
