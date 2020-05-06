package io.changock.driver.mongo.springdata.v3.driver.decorator.operation.executable.find;

import io.changock.driver.api.lock.guard.decorator.Invokable;
import io.changock.driver.api.lock.guard.invoker.LockGuardInvoker;
import org.springframework.data.mongodb.core.ExecutableFindOperation;


public interface ExecutableFindDecorator<T> extends Invokable, ExecutableFindOperation.ExecutableFind<T>, FindWithCollectionDecorator<T>, FindWithProjectionDecorator<T>, FindDistinctDecorator {


  ExecutableFindOperation.ExecutableFind<T> getImpl();

  LockGuardInvoker getInvoker();
}
