package io.changock.driver.api.lock.guard.proxy;


import io.changock.driver.api.lock.LockManager;
import io.changock.driver.api.lock.guard.proxy.util.InterfaceType;
import io.changock.driver.api.lock.guard.proxy.util.InterfaceTypeImpl;
import io.changock.driver.api.lock.guard.proxy.util.InterfaceTypeImplNonLockGuarded;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.Serializable;

import static io.changock.util.test.ReflectionUtils.getImplementationFromLockGuardProxy;
import static io.changock.util.test.ReflectionUtils.isProxy;
import static org.junit.Assert.assertEquals;
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
  public void shouldReturnProxy() {
    assertTrue(isProxy(getRawProxy(new InterfaceTypeImpl(), InterfaceType.class)));
  }

  @Test
  public void shouldNotReturnProxy_ifImplClassNonLockGuarded() {
    assertFalse(isProxy(getRawProxy(new InterfaceTypeImplNonLockGuarded(), InterfaceType.class)));
  }

  @Test
  public void shouldReturnProxyWithRightImplementation() {
    Object implementation = getImplementationFromLockGuardProxy(getRawProxy(new InterfaceTypeImpl(), InterfaceType.class));
    assertEquals(InterfaceTypeImpl.class, implementation.getClass());
  }

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
