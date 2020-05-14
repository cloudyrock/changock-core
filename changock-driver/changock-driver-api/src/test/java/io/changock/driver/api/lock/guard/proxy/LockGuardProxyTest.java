package io.changock.driver.api.lock.guard.proxy;

import io.changock.driver.api.lock.LockManager;
import io.changock.driver.api.lock.guard.proxy.util.InterfaceType;
import io.changock.driver.api.lock.guard.proxy.util.InterfaceTypeImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.verification.Times;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import static io.changock.driver.api.lock.guard.proxy.ReflectionUtils.isProxy;
import static org.junit.Assert.assertTrue;


public class LockGuardProxyTest {

  private LockManager lockManager;
  private InterfaceType proxy;

  @Before
  public void before() {
    lockManager = mock(LockManager.class);
    proxy = new LockGuardProxyFactory(lockManager).getProxy(new InterfaceTypeImpl(), InterfaceType.class);
  }

  //SHOULD RETURN PROXY

  @Test
  public void shouldReturnProxy() {
    assertTrue(isProxy(proxy.getGuardedImpl()));
  }

  @Test
  public void shouldReturnProxy_IfMethodAnnotatedWithNonLockGuardDefault() {
    assertTrue(isProxy(proxy.getGuardedImplWithAnnotationDefault()));
  }

  @Test
  public void shouldReturnProxy_IfMethodAnnotatedWithNonLockGuardMethod() {
    assertTrue(isProxy(proxy.getGuardedImplWithAnnotationMethod()));
  }

  // SHOULD NOT RETURN PROXY

  @Test
  public void shouldNotReturnProxy_IfReturningClassIsNotAnInterface() {
    assertFalse(isProxy(proxy.getNontInterfacedClass()));
  }

  @Test
  public void shouldNotReturnProxy_IfReturningIsString() {
    assertFalse(isProxy(proxy.getString()));
  }

  @Test
  public void shouldNotReturnProxy_IfReturningIsPrimitive() {
    assertFalse(isProxy(proxy.getPrimitive()));
  }

  @Test
  public void shouldNotReturnProxy_IfReturningIsPrimitiveWrapper() {
    assertFalse(isProxy(proxy.getPrimitiveWrapper()));
  }

  @Test
  public void shouldNotReturnProxy_IfReturningIsClassType() {
    assertFalse(isProxy(proxy.getClassType()));
  }

  @Test
  public void shouldNotReturnProxy_IfReturningIsAnnotated() {
    assertFalse(isProxy(proxy.getNonGuardedImpl()));
  }

  @Test
  public void shouldNotReturnProxy_WhenMethodNonLockGuardMethod_IfReturningIsAnnotated() {
    assertFalse(isProxy(proxy.getNonGuardedImplWithAnnotationMethod()));
  }

  @Test
  public void shouldNotReturnProxy_WhenMethodNonLockGuardNone() {
    assertFalse(isProxy(proxy.getGuardedImplWithAnnotationNone()));
  }

  // SHOULD BE LOCK GUARDED

  @Test
  public void shouldBeLockGuarded() {
    proxy.getGuardedImpl();
    verify(lockManager, new Times(1)).ensureLockDefault();
  }

  @Test
  public void shouldBeLockGuarded_ifVoidMethod() {
    proxy.voidMethod();
    verify(lockManager, new Times(1)).ensureLockDefault();
  }

  // SHOULD NOT BE LOCK-GUARDED

  @Test
  public void shouldNotBeLockGuarded_IfAnnotated() {
    proxy.callMethodNoLockGuarded();
    verify(lockManager, new Times(0)).ensureLockDefault();
  }

  @Test
  public void shouldNotBeLockGuarded_IfMethodAnnotatedWithNonLockGuardDefault() {
    proxy.getGuardedImplWithAnnotationDefault();
    verify(lockManager, new Times(0)).ensureLockDefault();
  }


  @Test
  public void shouldNotBeLockGuarded_IfMethodAnnotatedWithNonLockGuardMethod() {
    proxy.getGuardedImplWithAnnotationMethod();
    verify(lockManager, new Times(0)).ensureLockDefault();
  }

  @Test
  public void shouldNotBeLockGuarded_WhenMethodNonLockGuardNone() {
    proxy.getGuardedImplWithAnnotationNone();
    verify(lockManager, new Times(0)).ensureLockDefault();
  }

  @Test
  public void shouldNotBeLockGuarded_WhenMethodNonLockGuardMethod_IfReturningIsAnnotated() {
    proxy.getNonGuardedImplWithAnnotationMethod();
    verify(lockManager, new Times(0)).ensureLockDefault();
  }

}
