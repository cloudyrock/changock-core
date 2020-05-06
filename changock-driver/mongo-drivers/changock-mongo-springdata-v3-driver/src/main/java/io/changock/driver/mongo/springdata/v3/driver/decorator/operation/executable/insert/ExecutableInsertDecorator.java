package io.changock.driver.mongo.springdata.v3.driver.decorator.operation.executable.insert;

import io.changock.driver.api.lock.guard.decorator.Invokable;
import org.springframework.data.mongodb.core.ExecutableInsertOperation;

public interface ExecutableInsertDecorator<T> extends
    Invokable,
    ExecutableInsertOperation.ExecutableInsert<T>,
    TerminatingInsertDecorator<T>,
    InsertWithCollectionDecorator<T>,
    InsertWithBulkModeDecorator<T> {

  ExecutableInsertOperation.ExecutableInsert<T> getImpl();
}
