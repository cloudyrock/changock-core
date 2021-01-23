package com.github.cloudyrock.standalone;

import com.github.cloudyrock.mongock.runner.core.event.MigrationResult;
import com.github.cloudyrock.standalone.event.StandaloneEventPublisher;
import com.github.cloudyrock.standalone.event.StandaloneMigrationSuccessEvent;
import com.github.cloudyrock.standalone.event.StandaloneMigrationFailureEvent;
import org.junit.Assert;
import org.junit.Test;

public class StandaloneEventPublisherTest {

  @Test
  public void shouldCallStartedListener() {
    Listener listener = new Listener();
    new StandaloneEventPublisher(listener::startedListener, listener::successListener,listener::failListener).publishMigrationStarted();
    Assert.assertTrue(listener.isStartedCalled());
    Assert.assertFalse(listener.isSuccessCalled());
    Assert.assertFalse(listener.isFailCalled());
  }

  @Test
  public void shouldCallSuccessListener() {
    Listener listener = new Listener();
    new StandaloneEventPublisher(listener::startedListener, listener::successListener,listener::failListener).publishMigrationSuccessEvent(new MigrationResult());
    Assert.assertFalse(listener.isStartedCalled());
    Assert.assertTrue(listener.isSuccessCalled());
    Assert.assertFalse(listener.isFailCalled());
  }


  @Test
  public void shouldCallFailListener() {
    Listener listener = new Listener();
    RuntimeException ex = new RuntimeException();
    new StandaloneEventPublisher(listener::startedListener, listener::successListener,listener::failListener).publishMigrationFailedEvent(ex);
    Assert.assertFalse(listener.isStartedCalled());
    Assert.assertFalse(listener.isSuccessCalled());
    Assert.assertTrue(listener.isFailCalled());
    Assert.assertEquals(ex, listener.getException());
  }

  @Test
  public void shouldNotBreak_WhenSuccess_ifListenerIsNull() {
    new StandaloneEventPublisher(null, null,null).publishMigrationSuccessEvent(new MigrationResult());
  }

  @Test
  public void shouldNotBreak_WhenFail_ifListenerIsNull() {
    new StandaloneEventPublisher(null, null,null).publishMigrationFailedEvent(new Exception());
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

  void successListener(StandaloneMigrationSuccessEvent successEvent) {
    successCalled = true;
  }

  void failListener(StandaloneMigrationFailureEvent failureEvent) {
    failCalled = true;
    this.exception = failureEvent.getException();
  }

  public boolean isStartedCalled() {
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
