package io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.update;

import io.changock.driver.api.lock.guard.decorator.Invokable;
import org.springframework.data.mongodb.core.ExecutableUpdateOperation;

public interface TerminatingFindAndReplaceDecorator<T> extends Invokable, ExecutableUpdateOperation.TerminatingFindAndReplace<T> {

  ExecutableUpdateOperation.TerminatingFindAndReplace<T>  getImpl();

  @Override
  default T findAndReplaceValue() {
    return getInvoker().invoke(()-> getImpl().findAndReplaceValue());
  }
}
