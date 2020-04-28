package io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.find;

import io.changock.driver.mongo.springdata.v2.driver.decorator.util.Invokable;
import io.changock.driver.core.lock.guard.invoker.LockGuardInvoker;
import org.springframework.data.mongodb.core.ExecutableFindOperation;


public interface ExecutableFindDecorator<T> extends Invokable, ExecutableFindOperation.ExecutableFind<T>, FindWithCollectionDecorator<T>, FindWithProjectionDecorator<T>, FindDistinctDecorator {


  ExecutableFindOperation.ExecutableFind<T> getImpl();

  LockGuardInvoker getInvoker();
}
