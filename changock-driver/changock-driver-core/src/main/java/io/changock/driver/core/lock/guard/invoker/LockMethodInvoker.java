package io.changock.driver.core.lock.guard.invoker;

import io.changock.driver.api.lock.LockManager;

import java.util.function.Supplier;

public class LockMethodInvoker implements MethodInvoker {

  private final LockManager lockManager;

  public LockMethodInvoker(LockManager lockManager) {
    this.lockManager = lockManager;
  }

  @Override
  public <T> T invoke(Supplier<T> supplier) {
    lockManager.ensureLockDefault();
    return supplier.get();
  }

  @Override
  public void invoke(VoidSupplier supplier) {
    lockManager.ensureLockDefault();
    supplier.execute();
  }

}
