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

}


class Listener {

  private boolean successCalled = false;
  private boolean failCalled = false;
  private Exception exception;

  public void successListener() {
    successCalled = true;
  }

  public void failListener(Exception ex) {
    failCalled = true;
    this.exception = ex;
  }

  public boolean isSuccessCalled() {
    return successCalled;
  }

  public boolean isFailCalled() {
    return failCalled;
  }

  public Exception getException() {
    return exception;
  }
}
