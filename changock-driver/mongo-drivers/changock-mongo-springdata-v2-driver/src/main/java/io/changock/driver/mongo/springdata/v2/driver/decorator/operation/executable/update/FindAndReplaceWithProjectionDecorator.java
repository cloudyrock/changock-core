package io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.update;

import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.update.impl.FindAndReplaceWithOptionsDecoratorImpl;
import io.changock.driver.api.lock.guard.decorator.Invokable;
import org.springframework.data.mongodb.core.ExecutableUpdateOperation;

public interface FindAndReplaceWithProjectionDecorator<T>  extends Invokable, ExecutableUpdateOperation.FindAndReplaceWithProjection<T>, FindAndReplaceWithOptionsDecorator<T> {

  ExecutableUpdateOperation.FindAndReplaceWithProjection<T> getImpl();

  @Override
  default  <R> ExecutableUpdateOperation.FindAndReplaceWithOptions<R> as(Class<R> resultType) {
    return new FindAndReplaceWithOptionsDecoratorImpl<>(getInvoker().invoke(()-> getImpl().as(resultType)), getInvoker());
  }
}
