package io.changock.driver.mongo.springdata.v3.driver.decorator.operation.executable.update;

import io.changock.driver.mongo.springdata.v3.driver.decorator.operation.executable.update.impl.TerminatingFindAndModifyDecoratorImpl;
import io.changock.driver.api.lock.guard.decorator.Invokable;
import org.springframework.data.mongodb.core.ExecutableUpdateOperation;
import org.springframework.data.mongodb.core.FindAndModifyOptions;

public interface FindAndModifyWithOptionsDecorator<T> extends Invokable, ExecutableUpdateOperation.FindAndModifyWithOptions<T> {

  ExecutableUpdateOperation.FindAndModifyWithOptions<T>  getImpl();


  @Override
  default ExecutableUpdateOperation.TerminatingFindAndModify<T> withOptions(FindAndModifyOptions options) {
    return new TerminatingFindAndModifyDecoratorImpl<>(getInvoker().invoke(()-> getImpl().withOptions(options)), getInvoker());
  }
}
