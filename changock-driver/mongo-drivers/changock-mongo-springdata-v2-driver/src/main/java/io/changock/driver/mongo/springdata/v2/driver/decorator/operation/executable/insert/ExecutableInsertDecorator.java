package io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.insert;

import io.changock.driver.mongo.springdata.v2.driver.decorator.util.Invokable;
import org.springframework.data.mongodb.core.ExecutableInsertOperation;

public interface ExecutableInsertDecorator<T> extends
    Invokable,
    ExecutableInsertOperation.ExecutableInsert<T>,
    TerminatingInsertDecorator<T>,
    InsertWithCollectionDecorator<T>,
    InsertWithBulkModeDecorator<T> {

  ExecutableInsertOperation.ExecutableInsert<T> getImpl();
}
