package io.changock.driver.mongo.springdata.v3.driver.decorator.operation.executable.remove;

import io.changock.driver.api.lock.guard.decorator.Invokable;
import org.springframework.data.mongodb.core.ExecutableRemoveOperation;

public interface ExecutableRemoveDecorator<T> extends Invokable, ExecutableRemoveOperation.ExecutableRemove<T>, RemoveWithCollectionDecorator<T> {
}
