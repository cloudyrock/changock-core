package com.github.cloudyrock.mongock.driver.api.lock.guard.proxy;

import com.github.cloudyrock.mongock.driver.api.lock.LockManager;
import javassist.util.proxy.MethodHandler;

import java.lang.reflect.Method;

public class LockGuardMethodHandler<T>  implements MethodHandler {

  private final LockGuardProxy<T> lockGuardProxy;

  public LockGuardMethodHandler(T implementation, LockManager lockManager, LockGuardProxyFactory proxyFactory) {
    this.lockGuardProxy = new LockGuardProxy<>(implementation, lockManager, proxyFactory);
  }
  @Override
  public Object invoke(Object proxy, Method method, Method method1, Object[] methodArgs) throws Throwable {
    return lockGuardProxy.invoke(proxy, method, methodArgs);
  }
}
