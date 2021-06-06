package com.github.cloudyrock.mongock.driver.api.lock.guard.proxy;

import com.github.cloudyrock.mongock.NonLockGuarded;
import com.github.cloudyrock.mongock.NonLockGuardedType;
import com.github.cloudyrock.mongock.driver.api.lock.LockManager;

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

  private static boolean shouldTryProxyReturn(List<NonLockGuardedType> methodNoGuardedLockTypes) {
    return !methodNoGuardedLockTypes.contains(NonLockGuardedType.RETURN) && !methodNoGuardedLockTypes.contains(NonLockGuardedType.NONE);
  }

  private static boolean shouldMethodBeLockGuarded(List<NonLockGuardedType> noGuardedLockTypes) {
    return !noGuardedLockTypes.contains(NonLockGuardedType.METHOD) && !noGuardedLockTypes.contains(NonLockGuardedType.NONE);
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    NonLockGuarded nonLockGuarded = method.getAnnotation(NonLockGuarded.class);
    List<NonLockGuardedType> noGuardedLockTypes = nonLockGuarded != null ? Arrays.asList(nonLockGuarded.value()) : Collections.emptyList();
    if (shouldMethodBeLockGuarded(noGuardedLockTypes)) {
      lockManager.ensureLockDefault();
    }
    return shouldTryProxyReturn(noGuardedLockTypes)
        ? proxyFactory.getRawProxy(method.invoke(implementation, args), method.getReturnType())
        : method.invoke(implementation, args);
  }


}
