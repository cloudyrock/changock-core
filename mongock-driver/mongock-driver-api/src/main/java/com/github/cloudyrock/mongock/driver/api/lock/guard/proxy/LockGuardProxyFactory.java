package com.github.cloudyrock.mongock.driver.api.lock.guard.proxy;

import com.github.cloudyrock.mongock.NonLockGuarded;
import com.github.cloudyrock.mongock.driver.api.lock.LockManager;
import com.github.cloudyrock.mongock.driver.api.lock.guard.invoker.LockGuardInvokerImpl;
import com.github.cloudyrock.mongock.utils.Utils;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import org.objenesis.ObjenesisStd;

import java.lang.reflect.Modifier;
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
  private final LockGuardInvokerImpl defaultLockGuardInvoker;

  public LockGuardProxyFactory(LockManager lockManager) {
    this(lockManager, Collections.emptyList());
  }

  public LockGuardProxyFactory(LockManager lockManager, String... notProxiedPackagePrefixes) {
    this(lockManager, Arrays.asList(notProxiedPackagePrefixes));
  }

  public LockGuardProxyFactory(LockManager lockManager, Collection<String> notProxiedPackagePrefixes) {
    this.lockManager = lockManager;
    this.defaultLockGuardInvoker = new LockGuardInvokerImpl(lockManager);
    this.notProxiedPackagePrefixes = new ArrayList<>(notProxiedPackagePrefixes);
    this.notProxiedPackagePrefixes.addAll(javaPackagePrefixes);
  }

  @SuppressWarnings("unchecked")
  public <T> T getProxy(T targetObject, Class<? super T> interfaceType) {
    return (T) getRawProxy(targetObject, interfaceType);
  }

  @SuppressWarnings("unchecked")
  public Object getRawProxy(Object targetObject, Class<?> interfaceType) {
    return shouldBeLockGuardProxied(targetObject, interfaceType) ? createProxy(targetObject, interfaceType) : targetObject;
  }

  private boolean shouldBeLockGuardProxied(Object targetObject, Class<?> interfaceType) {

    return targetObject != null
        && !Modifier.isFinal(interfaceType.getModifiers())
        && isPackageProxiable(interfaceType.getPackage().getName())
        && !interfaceType.isAnnotationPresent(NonLockGuarded.class)
        && !targetObject.getClass().isAnnotationPresent(NonLockGuarded.class)
        && !Utils.isBasicTypeJDK(targetObject.getClass())
        && !Utils.isBasicTypeJDK(interfaceType);
  }

  private boolean isPackageProxiable(String packageName) {
    return notProxiedPackagePrefixes.stream().noneMatch(packageName::startsWith);
  }

  private Object createProxy(Object impl, Class<?> type) {
    if (type.isInterface()) {
      // TODO if do it with javassists, test fails with default methods. Ideally using just one mechanism
      return Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type}, new LockGuardProxy(impl, lockManager, this));
    } else {
      ProxyFactory proxyFactory = new ProxyFactory();
      proxyFactory.setSuperclass(type);
      Object proxyInstance = new ObjenesisStd()
          .getInstantiatorOf(proxyFactory.createClass())
          .newInstance();
      ((javassist.util.proxy.Proxy) proxyInstance).setHandler(getMethodHandlerFromInvocationHandler(impl));
      return proxyInstance;
    }
  }

  private <T> MethodHandler getMethodHandlerFromInvocationHandler(T impl) {
    return (o, method, method1, objects) -> new LockGuardProxy<T>(impl, lockManager, this);
  }
}
