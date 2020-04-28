package io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.remove.impl;

import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.remove.TerminatingRemoveDecorator;
import io.changock.driver.mongo.springdata.v2.driver.decorator.util.DecoratorBase;
import io.changock.driver.core.lock.guard.invoker.MethodInvoker;
import org.springframework.data.mongodb.core.ExecutableRemoveOperation;

public class TerminatingRemoveDecoratorImpl<T> extends DecoratorBase<ExecutableRemoveOperation.TerminatingRemove<T>> implements TerminatingRemoveDecorator<T> {

  public TerminatingRemoveDecoratorImpl(ExecutableRemoveOperation.TerminatingRemove<T> impl, MethodInvoker invoker) {
    super(impl, invoker);
  }

}
