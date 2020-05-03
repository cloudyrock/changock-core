package io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.remove;

import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.remove.impl.TerminatingRemoveDecoratorImpl;
import io.changock.driver.api.lock.guard.decorator.Invokable;
import org.springframework.data.mongodb.core.ExecutableRemoveOperation;
import org.springframework.data.mongodb.core.query.Query;

public interface RemoveWithQueryDecorator<T> extends Invokable, ExecutableRemoveOperation.RemoveWithQuery<T>, TerminatingRemoveDecorator<T> {

  ExecutableRemoveOperation.RemoveWithQuery<T> getImpl();

  @Override
  default ExecutableRemoveOperation.TerminatingRemove<T> matching(Query query) {
    return new TerminatingRemoveDecoratorImpl<>(getInvoker().invoke(() -> getImpl().matching(query)), getInvoker());
  }

}
