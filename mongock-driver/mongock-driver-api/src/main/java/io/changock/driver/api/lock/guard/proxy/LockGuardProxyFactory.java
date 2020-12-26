package io.changock.driver.api.lock.guard.proxy;

import io.changock.driver.api.lock.LockManager;
import com.github.cloudyrock.mongock.annotations.NonLockGuarded;
import io.changock.utils.Utils;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class LockGuardProxyFactory {

  private final static List<String> javaPackagePrefixes = Arrays.asList("java.", "com.sun.", "javax.", "jdk.internal.", "sun.");
  private final LockManager lockManager;
  private final Collection<String> notProxiedPackagePrefixes;

  public LockGuardProxyFactory(LockManager lockManager) {
    this(lockManager, Collections.emptyList());
  }

  public LockGuardProxyFactory(LockManager lockManager, String... notProxiedPackagePrefixes) {
    this(lockManager, Arrays.asList(notProxiedPackagePrefixes));
  }

  public LockGuardProxyFactory(LockManager lockManager, Collection<String> notProxiedPackagePrefixes) {
    this.lockManager = lockManager;
    this.notProxiedPackagePrefixes = new ArrayList<>(notProxiedPackagePrefixes);
    this.notProxiedPackagePrefixes.addAll(javaPackagePrefixes);
  }


  @SuppressWarnings("unchecked")
  public <T> T getProxy(T targetObject, Class<? super T> interfaceType) {
    return (T) getRawProxy(targetObject, interfaceType);
  }

  @SuppressWarnings("unchecked")
  public Object getRawProxy(Object targetObject, Class interfaceType) {
    return shouldBeLockGuardProxied(targetObject, interfaceType) ? createProxy(targetObject, interfaceType) : targetObject;
  }

  private boolean shouldBeLockGuardProxied(Object targetObject, Class interfaceType) {
    return targetObject != null
        && interfaceType.isInterface()
        && isPackageProxiable(interfaceType.getPackage().getName())
        && !interfaceType.isAnnotationPresent(NonLockGuarded.class)
        && !targetObject.getClass().isAnnotationPresent(NonLockGuarded.class)
        && !Utils.isBasicTypeJDK(targetObject.getClass())
        && !Utils.isBasicTypeJDK(interfaceType);
  }

  private  boolean isPackageProxiable(String packageName) {
    return notProxiedPackagePrefixes.stream().noneMatch(packageName::startsWith);
  }

  private Object createProxy(Object r, Class type) {
    return Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type}, new LockGuardProxy(r, lockManager, this));
  }
}
