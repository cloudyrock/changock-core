package io.changock.driver.api.lock.guard.proxy;

import io.changock.driver.api.lock.LockManager;
import io.changock.driver.api.lock.guard.NonDecorable;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class LockGuardProxy<T> implements InvocationHandler {

  private final LockManager lockManager;
  private final T implementation;
  private final LockGuardProxyFactory proxyFactory;

  public LockGuardProxy(T implementation, LockManager lockManager, LockGuardProxyFactory proxyFactory) {
    this.implementation = implementation;
    this.lockManager = lockManager;
    this.proxyFactory = proxyFactory;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    lockManager.ensureLockDefault();
    Class<?> returningType = method.getReturnType();
    Object result = method.invoke(implementation, args);
    return isDecorableMethod(method, returningType, result) ? proxyFactory.getRawProxy(result, returningType) : result;
  }

  private boolean isDecorableMethod(Method method, Class<?> returningType, Object result) {
    return result != null && returningType.isInterface()  && !method.isAnnotationPresent(NonDecorable.class);
  }


}
