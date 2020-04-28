package io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.mapreduce;

import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.mapreduce.impl.ExecutableMapReduceDecoratorImpl;
import io.changock.driver.core.lock.guard.invoker.MethodInvoker;
import org.springframework.data.mongodb.core.ExecutableMapReduceOperation;

public interface MapReduceWithReduceFunctionDecorator<T> extends ExecutableMapReduceOperation.MapReduceWithReduceFunction<T> {

    ExecutableMapReduceOperation.MapReduceWithReduceFunction<T> getImpl();

    MethodInvoker getInvoker();

    //TODO IMPLEMENT THIS DECORATOR
    @Override
    default ExecutableMapReduceOperation.ExecutableMapReduce<T> reduce(String reduceFunction) {
        return getInvoker().invoke(() -> new ExecutableMapReduceDecoratorImpl<>(getImpl().reduce(reduceFunction), getInvoker()));
    }
}
