package io.changock.driver.mongo.springdata.v2.driver.decorator.impl;

import io.changock.driver.mongo.springdata.v2.driver.decorator.SessionCallbackDecorator;
import io.changock.driver.core.lock.guard.invoker.LockGuardInvoker;
import org.springframework.data.mongodb.core.SessionCallback;

public class SessionCallbackDecoratorImpl<T> implements SessionCallbackDecorator<T> {

    private final SessionCallback<T> impl;
    private final LockGuardInvoker invoker;

    public SessionCallbackDecoratorImpl(SessionCallback<T> impl, LockGuardInvoker invoker) {
        this.impl = impl;
        this.invoker = invoker;
    }
    @Override
    public SessionCallback getImpl() {
        return impl;
    }

    @Override
    public LockGuardInvoker getInvoker() {
        return invoker;
    }
}
