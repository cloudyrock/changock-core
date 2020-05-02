package io.changock.driver.api.lock.guard.proxy;

import io.changock.driver.api.lock.LockManager;
import io.changock.migration.api.annotations.NonLockGuarded;
import io.changock.migration.api.annotations.NonLockGuardedType;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

//TODO add tests
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
    NonLockGuarded nonLockGuarded = method.getAnnotation(NonLockGuarded.class);
    List<NonLockGuardedType> noGuardedLockTypes = nonLockGuarded != null ? Arrays.asList(nonLockGuarded.value()) : Collections.emptyList();
    if (shouldMethodBeLockGuarded(noGuardedLockTypes)) {
      lockManager.ensureLockDefault();
    }
    Class<?> returningType = method.getReturnType();
    Object result = method.invoke(implementation, args);
    return shouldReturnObjectBeGuarded(result, returningType, noGuardedLockTypes) ? proxyFactory.getRawProxy(result, returningType) : result;
  }

  private static boolean shouldReturnObjectBeGuarded(Object result, Class<?> returningType, List<NonLockGuardedType> methodNoGuardedLockTypes) {
    return result != null
        && returningType.isInterface()
        && !isJdk8StandardInterface(returningType)
        && !methodNoGuardedLockTypes.contains(NonLockGuardedType.RETURN)
        && !methodNoGuardedLockTypes.contains(NonLockGuardedType.NONE)
        && !returningType.isAnnotationPresent(NonLockGuarded.class);
  }

  private static boolean shouldMethodBeLockGuarded(List<NonLockGuardedType> noGuardedLockTypes) {
    return !noGuardedLockTypes.contains(NonLockGuardedType.METHOD) && !noGuardedLockTypes.contains(NonLockGuardedType.NONE);
  }

  // TODO returns true if it's any of the interfaces in these packages: java.util, java.lang and any JDK standard interface
  private static boolean isJdk8StandardInterface(Class<?> returningType) {
    return false;
  }


}
