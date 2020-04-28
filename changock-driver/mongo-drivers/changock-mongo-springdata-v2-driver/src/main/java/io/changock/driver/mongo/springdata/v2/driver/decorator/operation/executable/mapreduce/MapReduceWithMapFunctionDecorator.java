package io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.mapreduce;

import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.mapreduce.impl.MapReduceWithReduceFunctionDecoratorImpl;
import io.changock.driver.core.lock.guard.invoker.MethodInvoker;
import org.springframework.data.mongodb.core.ExecutableMapReduceOperation;

public interface MapReduceWithMapFunctionDecorator<T> extends ExecutableMapReduceOperation.MapReduceWithMapFunction<T> {

    ExecutableMapReduceOperation.MapReduceWithMapFunction<T> getImpl();

    MethodInvoker getInvoker();

    @Override
    default ExecutableMapReduceOperation.MapReduceWithReduceFunction<T> map(String mapFunction) {
        return getInvoker().invoke(()-> new MapReduceWithReduceFunctionDecoratorImpl<>(getImpl().map(mapFunction), getInvoker()));
    }
}
