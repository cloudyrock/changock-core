package io.changock.driver.mongo.springdata.v3.driver.decorator.operation.executable.find;

import io.changock.driver.mongo.springdata.v3.driver.decorator.operation.executable.find.impl.TerminatingDistinctDecoratorImpl;
import io.changock.driver.api.lock.guard.decorator.Invokable;
import org.springframework.data.mongodb.core.ExecutableFindOperation;

public interface FindDistinctDecorator extends Invokable, ExecutableFindOperation.FindDistinct {

  ExecutableFindOperation.FindDistinct getImpl();


  @Override
  default ExecutableFindOperation.TerminatingDistinct<Object> distinct(String field) {
    return new TerminatingDistinctDecoratorImpl<>(getInvoker().invoke(()-> getImpl().distinct(field)), getInvoker());
  }
}
