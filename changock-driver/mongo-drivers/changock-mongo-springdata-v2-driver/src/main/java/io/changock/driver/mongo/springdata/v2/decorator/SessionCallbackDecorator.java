package io.changock.driver.mongo.springdata.v2.decorator;

import io.changock.driver.core.interceptor.decorator.MethodInvoker;
import io.changock.driver.mongo.springdata.v2.decorator.impl.MongoOperationsDecoratorImpl;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.SessionCallback;

public interface SessionCallbackDecorator<T> extends SessionCallback<T> {


    SessionCallback getImpl();

    MethodInvoker getInvoker();

    @Override
    default T doInSession(MongoOperations operations) {
        return getInvoker().invoke(()-> (T)getImpl().doInSession(new MongoOperationsDecoratorImpl(operations, getInvoker())));
    }
}
