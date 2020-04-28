package io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.mapreduce.impl;

import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.mapreduce.MapReduceWithReduceFunctionDecorator;
import io.changock.driver.core.lock.guard.invoker.MethodInvoker;
import org.springframework.data.mongodb.core.ExecutableMapReduceOperation;

public class MapReduceWithReduceFunctionDecoratorImpl<T> implements MapReduceWithReduceFunctionDecorator<T> {

    private final ExecutableMapReduceOperation.MapReduceWithReduceFunction<T> impl;
    private final MethodInvoker invoker;

    public MapReduceWithReduceFunctionDecoratorImpl(ExecutableMapReduceOperation.MapReduceWithReduceFunction<T> impl,
                                                    MethodInvoker invoker) {
        this.impl = impl;
        this.invoker = invoker;
    }
    @Override
    public ExecutableMapReduceOperation.MapReduceWithReduceFunction<T> getImpl() {
        return impl;
    }

    @Override
    public MethodInvoker getInvoker() {
        return invoker;
    }
}
