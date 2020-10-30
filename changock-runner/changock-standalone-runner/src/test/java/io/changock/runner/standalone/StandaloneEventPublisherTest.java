package io.changock.runner.standalone;

import org.junit.Assert;
import org.junit.Test;

public class StandaloneEventPublisherTest {

  @Test
  public void shouldCallSuccessListener() {
    Listener listener = new Listener();
    new StandaloneEventPublisher(listener::successListener,listener::failListener).publishMigrationSuccessEvent();
    Assert.assertTrue(listener.isSuccessCalled());
    Assert.assertFalse(listener.isFailCalled());
  }


  @Test
  public void shouldCallFailListener() {
    Listener listener = new Listener();
    RuntimeException ex = new RuntimeException();
    new StandaloneEventPublisher(listener::successListener,listener::failListener).publishMigrationFailedEvent(ex);
    Assert.assertFalse(listener.isSuccessCalled());
    Assert.assertTrue(listener.isFailCalled());
    Assert.assertEquals(ex, listener.getException());
  }

  @Test
  public void shouldNotBreak_WhenSuccess_ifListenerIsNull() {
    new StandaloneEventPublisher(null,null).publishMigrationSuccessEvent();
  }

  @Test
  public void shouldNotBreak_WhenFail_ifListenerIsNull() {
    new StandaloneEventPublisher(null,null).publishMigrationFailedEvent(new Exception());
  }

}


class Listener {

  private boolean successCalled = false;
  private boolean failCalled = false;
  private Exception exception;

  void successListener() {
    successCalled = true;
  }

  void failListener(Exception ex) {
    failCalled = true;
    this.exception = ex;
  }

  boolean isSuccessCalled() {
    return successCalled;
  }

  boolean isFailCalled() {
    return failCalled;
  }

  Exception getException() {
    return exception;
  }
}
