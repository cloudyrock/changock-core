package io.changock.driver.mongo.springdata.v2.driver.decorator;

import io.changock.driver.mongo.springdata.v2.driver.decorator.impl.MongoOperationsDecoratorImpl;
import io.changock.driver.api.lock.guard.invoker.LockGuardInvoker;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.SessionCallback;

public interface SessionCallbackDecorator<T> extends SessionCallback<T> {


    SessionCallback getImpl();

    LockGuardInvoker getInvoker();

    @Override
    default T doInSession(MongoOperations operations) {
        return getInvoker().invoke(()-> (T)getImpl().doInSession(new MongoOperationsDecoratorImpl(operations, getInvoker())));
    }
}
