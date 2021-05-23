package com.github.cloudyrock.standalone;

import com.github.cloudyrock.mongock.runner.core.event.result.MigrationResult;
import com.github.cloudyrock.mongock.runner.core.event.MongockEventPublisher;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StandaloneEventPublisherTest {

  @Test
  public void shouldCallStartedListener() {
    Listener listener = new Listener();
    new MongockEventPublisher(listener::startedListener, listener::successListener,listener::failListener).publishMigrationStarted();
    Assert.assertTrue(listener.isStartedCalled());
    Assert.assertFalse(listener.isSuccessCalled());
    Assert.assertFalse(listener.isFailCalled());
  }

  @Test
  public void shouldCallSuccessListener() {
    Listener listener = new Listener();
    new MongockEventPublisher(listener::startedListener, listener::successListener,listener::failListener).publishMigrationSuccessEvent(MigrationResult.successResult());
    Assert.assertFalse(listener.isStartedCalled());
    Assert.assertTrue(listener.isSuccessCalled());
    Assert.assertFalse(listener.isFailCalled());
  }


  @Test
  public void shouldCallFailListener() {
    Listener listener = new Listener();
    RuntimeException ex = new RuntimeException();
    new MongockEventPublisher(listener::startedListener, listener::successListener,listener::failListener).publishMigrationFailedEvent(ex);
    Assert.assertFalse(listener.isStartedCalled());
    Assert.assertFalse(listener.isSuccessCalled());
    Assert.assertTrue(listener.isFailCalled());
    assertEquals(ex, listener.getException());
  }

  @Test
  public void shouldNotBreak_WhenSuccess_ifListenerIsNull() {
    new MongockEventPublisher(null, null,null).publishMigrationSuccessEvent(MigrationResult.successResult());
  }

  @Test
  public void shouldNotBreak_WhenFail_ifListenerIsNull() {
    new MongockEventPublisher(null, null,null).publishMigrationFailedEvent(new Exception());
  }

}


class Listener {

  private boolean startedCalled = false;
  private boolean successCalled = false;
  private boolean failCalled = false;
  private Exception exception;

  void startedListener(){
    startedCalled = true;
  }

  void successListener(MigrationResult successEvent) {
    successCalled = true;
  }

  void failListener(Exception exception) {
    failCalled = true;
    this.exception = exception;
  }

  boolean isStartedCalled() {
    return startedCalled;
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
