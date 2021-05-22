package com.github.cloudyrock.springboot;


import com.github.cloudyrock.mongock.runner.core.event.MigrationResult;
import com.github.cloudyrock.springboot.base.events.SpringEventPublisher;
import com.github.cloudyrock.springboot.base.events.SpringMigrationFailureEvent;
import com.github.cloudyrock.springboot.base.events.SpringMigrationSuccessEvent;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.internal.verification.Times;
import org.springframework.context.ApplicationEventPublisher;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

public class SpringEventPublisherTest {

  @Test
  public void shouldCallSuccessListener() {
    ApplicationEventPublisher applicationEventPublisher = Mockito.mock(ApplicationEventPublisher.class);
    new SpringEventPublisher(applicationEventPublisher).publishMigrationSuccessEvent(new MigrationResult());

    ArgumentCaptor<SpringMigrationSuccessEvent> eventCaptor = ArgumentCaptor.forClass(SpringMigrationSuccessEvent.class);
    verify(applicationEventPublisher, new Times(1)).publishEvent(eventCaptor.capture());
    Assert.assertTrue(eventCaptor.getValue() instanceof SpringMigrationSuccessEvent);
  }

  @Test
  public void shouldCallFailListener() {
    RuntimeException ex = new RuntimeException();
    ApplicationEventPublisher applicationEventPublisher = Mockito.mock(ApplicationEventPublisher.class);
    new SpringEventPublisher(applicationEventPublisher).publishMigrationFailedEvent(ex);

    ArgumentCaptor<SpringMigrationFailureEvent> eventCaptor = ArgumentCaptor.forClass(SpringMigrationFailureEvent.class);
    verify(applicationEventPublisher, new Times(1)).publishEvent(eventCaptor.capture());
    assertEquals(ex, eventCaptor.getValue().getException());
  }



}
