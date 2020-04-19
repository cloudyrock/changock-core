package io.changock.driver.mongo.springdata.v2.decorator.impl;

import io.changock.driver.core.lock.interceptor.decorator.MethodInvoker;
import io.changock.driver.mongo.springdata.v2.decorator.MongoOperationsDecorator;
import org.springframework.data.mongodb.core.MongoOperations;

public class MongoOperationsDecoratorImpl implements MongoOperationsDecorator {

    private final MongoOperations impl;
    private final MethodInvoker invoker;

    public MongoOperationsDecoratorImpl(MongoOperations impl, MethodInvoker invoker) {
        this.impl = impl;
        this.invoker = invoker;
    }

    @Override
    public MongoOperations getImpl() {
        return impl;
    }

    @Override
    public MethodInvoker getInvoker() {
        return invoker;
    }
}
