package io.changock.driver.mongo.springdata.v2.decorator;

import io.changock.driver.core.interceptor.decorator.MethodInvoker;
import io.changock.driver.mongo.springdata.v2.decorator.impl.IndexOperationsDecoratorImpl;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.index.IndexOperationsProvider;

public interface IndexOperationsProviderDecorator extends IndexOperationsProvider {

    IndexOperationsProvider getimpl();

    MethodInvoker getInvoker();

    @Override
    default IndexOperations indexOps(String collectionName) {
        return new IndexOperationsDecoratorImpl(getInvoker().invoke(()-> getimpl().indexOps(collectionName)), getInvoker());
    }
}
