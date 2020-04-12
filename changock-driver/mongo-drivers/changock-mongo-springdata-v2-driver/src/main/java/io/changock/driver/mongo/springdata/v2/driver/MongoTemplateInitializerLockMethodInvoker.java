package io.changock.driver.mongo.springdata.v2.driver;

import io.changock.driver.api.lock.LockManager;
import io.changock.driver.core.decorator.MethodInvoker;
import io.changock.driver.core.decorator.VoidSupplier;

import java.util.function.Supplier;

public class MongoTemplateInitializerLockMethodInvoker implements MethodInvoker {
    private final LockManager lockManager;

    public MongoTemplateInitializerLockMethodInvoker(LockManager lockManager) {
        this.lockManager = lockManager;
    }

    @Override
    public <T> T invoke(Supplier<T> supplier) {
        lockManager.acquireLockDefault();
        return supplier.get();
    }

    @Override
    public void invoke(VoidSupplier supplier) {
        lockManager.acquireLockDefault();
        supplier.execute();
    }
}
