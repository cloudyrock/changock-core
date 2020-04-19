package io.changock.driver.mongo.springdata.v2.decorator;

import io.changock.driver.core.lock.interceptor.decorator.MethodInvoker;
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
