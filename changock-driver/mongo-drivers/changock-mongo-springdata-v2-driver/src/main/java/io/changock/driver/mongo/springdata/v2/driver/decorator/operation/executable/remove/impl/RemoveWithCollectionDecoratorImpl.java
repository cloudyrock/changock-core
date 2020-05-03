package io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.remove.impl;

import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.remove.RemoveWithCollectionDecorator;
import io.changock.driver.api.lock.guard.decorator.DecoratorBase;
import io.changock.driver.api.lock.guard.invoker.LockGuardInvoker;
import org.springframework.data.mongodb.core.ExecutableRemoveOperation;

public class RemoveWithCollectionDecoratorImpl<T>
    extends DecoratorBase<ExecutableRemoveOperation.RemoveWithCollection<T>>
  implements RemoveWithCollectionDecorator<T> {

  public RemoveWithCollectionDecoratorImpl(ExecutableRemoveOperation.RemoveWithCollection<T> impl, LockGuardInvoker invoker) {
    super(impl, invoker);
  }
}
