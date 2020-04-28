package io.changock.driver.core.lock.guard.invoker;

import java.util.function.Supplier;

public interface LockGuardInvoker {
  <T> T invoke(Supplier<T> supplier);

  void invoke(VoidSupplier supplier);
}
