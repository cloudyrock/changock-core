package com.github.cloudyrock.mongock.driver.api.lock.guard.proxy;


import com.github.cloudyrock.mongock.driver.api.lock.LockManager;
import com.github.cloudyrock.mongock.driver.api.lock.guard.proxy.util.ContentHandlerFactoryImpl;
import com.github.cloudyrock.mongock.driver.api.lock.guard.proxy.util.InterfaceType;
import com.github.cloudyrock.mongock.driver.api.lock.guard.proxy.util.InterfaceTypeImpl;
import com.github.cloudyrock.mongock.driver.api.lock.guard.proxy.util.InterfaceTypeImplNonLockGuarded;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.Serializable;
import java.net.ContentHandlerFactory;
import java.util.ArrayList;
import java.util.List;

import static com.github.cloudyrock.mongock.util.test.ReflectionUtils.isProxy;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class LockGuardProxyFactoryTest {

  private LockManager lockManager;
  private LockGuardProxyFactory lockGuardProxyFactory;

  @Before
  public void before() {
    lockManager = Mockito.mock(LockManager.class);
    lockGuardProxyFactory = new LockGuardProxyFactory(lockManager);
  }

  private Object getRawProxy(Object o, Class interfaceType) {
    return lockGuardProxyFactory.getRawProxy(o, interfaceType);
  }

  @Test
  public void shouldNotReturnProxy_IfInterfaceTypePackageIsJava() {
    assertFalse(isProxy(getRawProxy(new ArrayList<>(), List.class)));
    assertFalse(isProxy(getRawProxy(new ContentHandlerFactoryImpl(), ContentHandlerFactory.class)));
  }

  @Test
  public void shouldNotReturnProxy_IfInterfaceTypeisJavaNet() {
    lockGuardProxyFactory = new LockGuardProxyFactory(lockManager, InterfaceType.class.getPackage().getName().substring(0, 12));
    assertFalse(isProxy(getRawProxy(new InterfaceTypeImpl(), InterfaceType.class)));
  }


  @Test
  public void shouldReturnProxy2() {
    assertFalse(isProxy(getRawProxy(new ArrayList<>(), List.class)));
  }

  @Test
  public void shouldReturnProxy() {
    assertTrue(isProxy(getRawProxy(new InterfaceTypeImpl(), InterfaceType.class)));
  }

  @Test
  public void shouldNotReturnProxy_ifImplClassNonLockGuarded() {
    assertFalse(isProxy(getRawProxy(new InterfaceTypeImplNonLockGuarded(), InterfaceType.class)));
  }

  //failing in local but not in CI
//  @Test
//  public void shouldReturnProxyWithRightImplementation() {
//    Object implementation = getImplementationFromLockGuardProxy(getRawProxy(new InterfaceTypeImpl(), InterfaceType.class));
//    assertEquals(InterfaceTypeImpl.class, implementation.getClass());
//  }

  @Test
  public void ShouldReturnNull_ifTargetIsNull() {
    assertNull(getRawProxy(null, InterfaceTypeImpl.class));
  }

  @Test
  public void ShouldNotReturnProxy_ifTargetNotInterface() {
    assertFalse(isProxy(getRawProxy(new InterfaceTypeImpl(), InterfaceTypeImpl.class)));
  }

  @Test
  public void ShouldNotReturnProxy_ifPrimitive() {
    assertFalse(isProxy(getRawProxy(1, Comparable.class)));
  }

  @Test
  public void ShouldNotReturnProxy_ifPrimitiveWrapper() {
    assertFalse(isProxy(getRawProxy(new Integer(1), Comparable.class)));
  }

  @Test
  public void ShouldNotReturnProxy_ifString() {
    assertFalse(isProxy(getRawProxy("anyString", Serializable.class)));
  }

  @Test
  public void ShouldNotReturnProxy_ifClass() {
    assertFalse(isProxy(getRawProxy(InterfaceTypeImpl.class, Serializable.class)));
  }

}
