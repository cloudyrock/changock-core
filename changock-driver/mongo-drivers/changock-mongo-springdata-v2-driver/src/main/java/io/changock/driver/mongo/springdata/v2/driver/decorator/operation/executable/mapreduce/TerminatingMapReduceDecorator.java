package io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.mapreduce;

import io.changock.driver.core.lock.guard.invoker.MethodInvoker;
import org.springframework.data.mongodb.core.ExecutableMapReduceOperation;

import java.util.List;

public interface TerminatingMapReduceDecorator<T> extends ExecutableMapReduceOperation.TerminatingMapReduce<T> {
    ExecutableMapReduceOperation.TerminatingMapReduce<T> getImpl();

    MethodInvoker getInvoker();

    @Override
    default List<T> all() {
        return getInvoker().invoke(() -> getImpl().all());
    }
}
