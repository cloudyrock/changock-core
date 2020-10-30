package io.changock.runner.spring.util;


import io.changock.runner.spring.util.events.MongockMigrationFailEvent;
import io.changock.runner.spring.util.events.MongockMigrationSuccessEvent;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.internal.verification.Times;
import org.springframework.context.ApplicationEventPublisher;

import static org.mockito.Mockito.verify;

public class SpringEventPublisherTest {

  @Test
  public void shouldCallSuccessListener() {
    ApplicationEventPublisher applicationEventPublisher = Mockito.mock(ApplicationEventPublisher.class);
    new SpringEventPublisher(applicationEventPublisher).publishMigrationSuccessEvent();

    ArgumentCaptor<MongockMigrationSuccessEvent> eventCaptor = ArgumentCaptor.forClass(MongockMigrationSuccessEvent.class);
    verify(applicationEventPublisher, new Times(1)).publishEvent(eventCaptor.capture());
    Assert.assertTrue(eventCaptor.getValue() instanceof MongockMigrationSuccessEvent);
  }

  @Test
  public void shouldCallFailListener() {
    RuntimeException ex = new RuntimeException();
    ApplicationEventPublisher applicationEventPublisher = Mockito.mock(ApplicationEventPublisher.class);
    new SpringEventPublisher(applicationEventPublisher).publishMigrationFailedEvent(ex);

    ArgumentCaptor<MongockMigrationFailEvent> eventCaptor = ArgumentCaptor.forClass(MongockMigrationFailEvent.class);
    verify(applicationEventPublisher, new Times(1)).publishEvent(eventCaptor.capture());
    Assert.assertEquals(ex, eventCaptor.getValue().getException());
  }

  @Test
  public void shouldNotBreak_WhenSuccess_ifListenerIsNull() {
    new SpringEventPublisher(null).publishMigrationSuccessEvent();
  }

  @Test
  public void shouldNotBreak_WhenFail_ifListenerIsNull() {
    new SpringEventPublisher(null).publishMigrationFailedEvent(new RuntimeException());
  }

}
