package io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.remove;

import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.remove.impl.RemoveWithQueryDecoratorImpl;
import io.changock.driver.mongo.springdata.v2.driver.decorator.util.Invokable;
import org.springframework.data.mongodb.core.ExecutableRemoveOperation;

public interface RemoveWithCollectionDecorator<T> extends Invokable, ExecutableRemoveOperation.RemoveWithCollection<T>, RemoveWithQueryDecorator<T> {

  ExecutableRemoveOperation.RemoveWithCollection<T> getImpl();

  @Override
  default ExecutableRemoveOperation.RemoveWithQuery<T> inCollection(String collection) {
    return new RemoveWithQueryDecoratorImpl<>(getInvoker().invoke(()-> getImpl().inCollection(collection)), getInvoker());
  }
}
