package io.changock.driver.api.lock.guard.proxy;

import io.changock.driver.api.lock.LockManager;
import io.changock.migration.api.exception.ChangockException;

import java.lang.reflect.Proxy;

//TODO tests
public class LockGuardProxyFactory {

  private final LockManager lockManager;

  public LockGuardProxyFactory(LockManager lockManager) {
    this.lockManager = lockManager;
  }

  @SuppressWarnings("unchecked")
  public Object getRawProxy(Object r, Class type) {
    if (r == null) {
      return null;
    }
    if (!type.isInterface()) {
      throw new ChangockException(String.format("Parameter of type [%s] must be an interface", type.getSimpleName()));
    }
    return Proxy.newProxyInstance(
        type.getClassLoader(),
        new Class<?>[]{type}, new LockGuardProxy(r, lockManager, this)
    );
  }

  @SuppressWarnings("unchecked")
  public <T> T getProxy(T r, Class<? super T> interfaceType) {
    return (T) getRawProxy(r, interfaceType);
  }
}
