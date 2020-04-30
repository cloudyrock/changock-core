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
  <R> R getRawProxy(Object r, Class<? super R> interfaceType) {
    return (R) Proxy.newProxyInstance(
        interfaceType.getClassLoader(),
        new Class<?>[]{interfaceType}, new LockGuardProxy(r, lockManager, this)
    );
  }

  public <R> R getProxy(R r, Class<? super R> interfaceType) {
    return getRawProxy(r, interfaceType);
  }
}
