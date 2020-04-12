package io.changock.driver.mongo.springdata.v2.decorator;

import io.changock.driver.core.decorator.MethodInvoker;
import io.changock.driver.mongo.springdata.v2.decorator.impl.MapReduceWithReduceFunctionDecoratorImpl;
import org.springframework.data.mongodb.core.ExecutableMapReduceOperation;

public interface MapReduceWithMapFunctionDecorator<T> extends ExecutableMapReduceOperation.MapReduceWithMapFunction<T> {

    ExecutableMapReduceOperation.MapReduceWithMapFunction<T> getImpl();

    MethodInvoker getInvoker();

    @Override
    default ExecutableMapReduceOperation.MapReduceWithReduceFunction<T> map(String mapFunction) {
        return getInvoker().invoke(()-> new MapReduceWithReduceFunctionDecoratorImpl<>(getImpl().map(mapFunction), getInvoker()));
    }
}
