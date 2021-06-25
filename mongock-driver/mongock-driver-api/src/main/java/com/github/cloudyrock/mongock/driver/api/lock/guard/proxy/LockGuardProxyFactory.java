package com.github.cloudyrock.mongock.driver.api.lock.guard.proxy;

import com.github.cloudyrock.mongock.NonLockGuarded;
import com.github.cloudyrock.mongock.driver.api.lock.LockManager;
import com.github.cloudyrock.mongock.driver.api.lock.guard.invoker.LockGuardInvokerImpl;
import com.github.cloudyrock.mongock.utils.Utils;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import org.objenesis.ObjenesisStd;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
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

    ProxyFactory proxyFactory = new ProxyFactory();
    if (type.isInterface()) {
      proxyFactory.setInterfaces(new Class<?>[]{type});
    } else {
      proxyFactory.setSuperclass(type);
    }
    Object proxyInstance = new ObjenesisStd()
        .getInstantiatorOf(proxyFactory.createClass())
        .newInstance();
    ((javassist.util.proxy.Proxy) proxyInstance).setHandler(new LockGuardMethodHandler<>(impl, lockManager, this));
    return proxyInstance;
  }

  public static boolean isProxy(Object obj) {
    return isProxyClass(obj.getClass());
  }

  public static boolean isProxyClass(Class<?> c) {
    return Proxy.isProxyClass(c) || ProxyFactory.isProxyClass(c);
  }

  public static void checkProxy(Object obj) {
    if(!isProxyClass(obj.getClass())) {
      throw new RuntimeException("Is not proxy");
    }
  }

}
