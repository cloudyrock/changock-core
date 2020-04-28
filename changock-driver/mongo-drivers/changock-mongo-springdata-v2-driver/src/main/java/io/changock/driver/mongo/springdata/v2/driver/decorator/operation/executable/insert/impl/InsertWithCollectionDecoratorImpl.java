package io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.insert.impl;

import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.insert.InsertWithCollectionDecorator;
import io.changock.driver.mongo.springdata.v2.driver.decorator.util.DecoratorBase;
import io.changock.driver.core.lock.guard.invoker.MethodInvoker;
import org.springframework.data.mongodb.core.ExecutableInsertOperation;

public class InsertWithCollectionDecoratorImpl<T>
    extends DecoratorBase<ExecutableInsertOperation.InsertWithCollection<T>>
    implements InsertWithCollectionDecorator<T> {
  public InsertWithCollectionDecoratorImpl(ExecutableInsertOperation.InsertWithCollection<T> impl, MethodInvoker invoker) {
    super(impl, invoker);
  }
}
