package io.changock.driver.api.lock.guard.proxy;

import io.changock.driver.api.lock.LockManager;
import io.changock.migration.api.annotations.NonLockGuarded;
import io.changock.migration.api.exception.ChangockException;

import java.lang.reflect.Proxy;

// Tests covered in IT for migrationExecutor and Builder
public class LockGuardProxyFactory {

  private final LockManager lockManager;

  public LockGuardProxyFactory(LockManager lockManager) {
    this.lockManager = lockManager;
  }


  @SuppressWarnings("unchecked")
  public <T> T getProxy(T r, Class<? super T> interfaceType) {
    return (T) getRawProxy(r, interfaceType);
  }

  @SuppressWarnings("unchecked")
  public Object getRawProxy(Object r, Class type) {
    if (!type.isInterface()) {
      throw new ChangockException(String.format("Parameter of type [%s] must be an interface", type.getSimpleName()));
    }
    return shouldBeLockGuardProxied(r, type) ? createProxy(r, type) : r;
  }

  private boolean shouldBeLockGuardProxied(Object r, Class type) {
    return r != null
        && !type.isAnnotationPresent(NonLockGuarded.class)
        && !r.getClass().isAnnotationPresent(NonLockGuarded.class);
  }

  private Object createProxy(Object r, Class type) {
    return Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type}, new LockGuardProxy(r, lockManager, this));
  }
}
