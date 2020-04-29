package io.changock.driver.core.lock.guard.proxy;

import io.changock.driver.api.lock.LockManager;
import io.changock.driver.core.lock.guard.NonDecorable;

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
    Class<?> returningType = method.getReturnType();
    Object result = method.invoke(implementation, args);
    return isDecorableMethod(method, returningType, result) ? getRawProxy(result, returningType, lockManager) : result;
  }

  private boolean isDecorableMethod(Method method, Class<?> returningType, Object result) {
    return result != null && returningType.isInterface()  && !method.isAnnotationPresent(NonDecorable.class);
  }

  @SuppressWarnings("unchecked")
  private static <R> R getRawProxy(Object r, Class<? super R> interfaceType, LockManager lockManager) {
    return (R) Proxy.newProxyInstance(
        interfaceType.getClassLoader(),
        new Class<?>[]{interfaceType}, new LockGuardProxy(r, lockManager)
    );
  }

  public static <R> R getProxy(R r, Class<? super R> interfaceType, LockManager lockManager) {
    return getRawProxy(r, interfaceType, lockManager);
  }
}
