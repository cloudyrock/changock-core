package io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.update;

import io.changock.driver.mongo.springdata.v2.driver.decorator.util.Invokable;
import org.springframework.data.mongodb.core.ExecutableUpdateOperation;

public interface TerminatingFindAndModifyDecorator<T> extends Invokable, ExecutableUpdateOperation.TerminatingFindAndModify<T> {

  ExecutableUpdateOperation.TerminatingFindAndModify<T> getImpl();

  @Override
  default T findAndModifyValue() {
    return getInvoker().invoke(()-> getImpl().findAndModifyValue());
  }
}
