package io.changock.driver.mongo.springdata.v2.decorator.impl;

import io.changock.driver.core.lock.interceptor.decorator.MethodInvoker;
import io.changock.driver.mongo.springdata.v2.decorator.MapReduceWithReduceFunctionDecorator;
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
