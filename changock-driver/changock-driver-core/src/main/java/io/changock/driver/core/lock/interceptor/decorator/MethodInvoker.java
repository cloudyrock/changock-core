package io.changock.driver.core.lock.interceptor.decorator;

import java.util.function.Supplier;

public interface MethodInvoker {
  <T> T invoke(Supplier<T> supplier);

  void invoke(VoidSupplier supplier);
}
