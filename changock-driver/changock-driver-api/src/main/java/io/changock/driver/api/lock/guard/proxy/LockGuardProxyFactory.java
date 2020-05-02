package io.changock.driver.api.lock.guard.proxy;

import io.changock.driver.api.lock.LockManager;

import java.lang.reflect.Proxy;

//TODO tests
public class LockGuardProxyFactory {

  private final LockManager lockManager;

  public LockGuardProxyFactory(LockManager lockManager) {
    this.lockManager = lockManager;
  }

  @SuppressWarnings("unchecked")
  Object getRawProxy(Object r, Class interfaceType) {
    return Proxy.newProxyInstance(
        interfaceType.getClassLoader(),
        new Class<?>[]{interfaceType}, new LockGuardProxy(r, lockManager, this)
    );
  }

  @SuppressWarnings("unchecked")
  public <T> T getProxy(T r, Class<? super T> interfaceType) {
    return (T)getRawProxy(r, interfaceType);
  }
}
