package io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.update.impl;

import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.update.TerminatingFindAndReplaceDecorator;
import io.changock.driver.mongo.springdata.v2.driver.decorator.util.DecoratorBase;
import io.changock.driver.core.lock.guard.invoker.MethodInvoker;
import org.springframework.data.mongodb.core.ExecutableUpdateOperation;

public class TerminatingFindAndReplaceDecoratorImpl<T> extends DecoratorBase<ExecutableUpdateOperation.TerminatingFindAndReplace<T>> implements TerminatingFindAndReplaceDecorator<T> {

  public TerminatingFindAndReplaceDecoratorImpl(ExecutableUpdateOperation.TerminatingFindAndReplace<T> impl, MethodInvoker invoker) {
    super(impl, invoker);
  }

}
