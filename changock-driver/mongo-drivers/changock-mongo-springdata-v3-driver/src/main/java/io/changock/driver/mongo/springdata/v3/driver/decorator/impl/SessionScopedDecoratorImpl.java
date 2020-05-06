package io.changock.driver.mongo.springdata.v3.driver.decorator.impl;

import io.changock.driver.mongo.springdata.v3.driver.decorator.SessionScopedDecorator;
import io.changock.driver.api.lock.guard.invoker.LockGuardInvoker;
import org.springframework.data.mongodb.core.SessionScoped;

public class SessionScopedDecoratorImpl implements SessionScopedDecorator {

    private final SessionScoped impl;
    private final LockGuardInvoker invoker;

    public SessionScopedDecoratorImpl(SessionScoped impl, LockGuardInvoker invoker) {
        this.impl = impl;
        this.invoker = invoker;
    }
    @Override
    public SessionScoped getImpl() {
        return impl;
    }

    @Override
    public LockGuardInvoker getInvoker() {
        return invoker;
    }
}
