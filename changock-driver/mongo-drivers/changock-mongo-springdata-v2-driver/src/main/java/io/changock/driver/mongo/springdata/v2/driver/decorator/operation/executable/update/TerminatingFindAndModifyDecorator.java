package io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.update;

import io.changock.driver.api.lock.guard.decorator.Invokable;
import org.springframework.data.mongodb.core.ExecutableUpdateOperation;

import java.util.Optional;

public interface TerminatingFindAndModifyDecorator<T> extends Invokable, ExecutableUpdateOperation.TerminatingFindAndModify<T> {

  ExecutableUpdateOperation.TerminatingFindAndModify<T> getImpl();

  @Override
  default Optional<T> findAndModify() {
    return getInvoker().invoke(()-> getImpl().findAndModify());
  }


  @Override
  default T findAndModifyValue() {
    return getInvoker().invoke(()-> getImpl().findAndModifyValue());
  }
}
