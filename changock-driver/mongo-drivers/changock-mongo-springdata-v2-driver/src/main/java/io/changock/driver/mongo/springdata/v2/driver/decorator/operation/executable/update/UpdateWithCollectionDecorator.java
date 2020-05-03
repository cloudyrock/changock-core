package io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.update;

import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.update.impl.UpdateWithQueryDecoratorImpl;
import io.changock.driver.api.lock.guard.decorator.Invokable;
import org.springframework.data.mongodb.core.ExecutableUpdateOperation;

public interface UpdateWithCollectionDecorator<T> extends Invokable, ExecutableUpdateOperation.UpdateWithCollection<T> {

  ExecutableUpdateOperation.UpdateWithCollection<T> getImpl();

  @Override
  default ExecutableUpdateOperation.UpdateWithQuery<T> inCollection(String collection) {
    return new UpdateWithQueryDecoratorImpl<>(getInvoker().invoke(()-> getImpl().inCollection(collection)), getInvoker());
  }
}
