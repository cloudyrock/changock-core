package io.changock.driver.core.lock.guard.invoker;

import java.util.function.Supplier;

public interface MethodInvoker {
  <T> T invoke(Supplier<T> supplier);

  void invoke(VoidSupplier supplier);
}
