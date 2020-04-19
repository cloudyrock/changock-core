package io.changock.driver.core.lock.guard.proxy;

import io.changock.driver.api.lock.LockManager;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class LockGuardProxy<T> implements InvocationHandler {

  private final LockManager lockManager;
  private final T implementation;

  public LockGuardProxy(T implementation, LockManager lockManager) {
    this.implementation = implementation;
    this.lockManager = lockManager;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    lockManager.ensureLockDefault();
    return method.invoke(implementation, args);
  }

  @SuppressWarnings("unchecked")
  public static <T> T getProxy(T t, Class<? super T> interfaceType, LockManager lockManager) {
    return (T) Proxy.newProxyInstance(
        interfaceType.getClassLoader(),
        new Class<?>[]{interfaceType}, new LockGuardProxy(t, lockManager)
    );
  }
}
