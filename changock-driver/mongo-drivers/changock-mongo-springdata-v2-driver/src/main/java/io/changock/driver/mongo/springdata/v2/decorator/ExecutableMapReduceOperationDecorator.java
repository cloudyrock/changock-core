package io.changock.driver.mongo.springdata.v2.decorator;

import io.changock.driver.core.decorator.MethodInvoker;
import io.changock.driver.mongo.springdata.v2.decorator.impl.MapReduceWithMapFunctionDecoratorImpl;
import org.springframework.data.mongodb.core.ExecutableMapReduceOperation;

public interface ExecutableMapReduceOperationDecorator extends ExecutableMapReduceOperation {

    ExecutableMapReduceOperation getImpl();

    MethodInvoker getInvoker();

    @Override
    default <T> MapReduceWithMapFunction<T> mapReduce(Class<T> domainType) {
        return getInvoker().invoke(()-> new MapReduceWithMapFunctionDecoratorImpl<>(getImpl().mapReduce(domainType), getInvoker()));
    }
}
