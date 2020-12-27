package com.github.cloudyrock.mongock.runner.core.executor;

import com.github.cloudyrock.mongock.ChangeLogItem;
import com.github.cloudyrock.mongock.exception.MongockException;
import com.github.cloudyrock.mongock.driver.api.lock.LockCheckException;
import com.github.cloudyrock.mongock.runner.core.event.EventPublisher;
import com.github.cloudyrock.mongock.runner.core.event.MigrationResult;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.internal.verification.Times;

import java.util.SortedSet;
import java.util.TreeSet;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MongockRunnerBaseTest {


  @Test
  public void shouldExecuteAllTheChangeLogsAndPublishRightEvent_whenNoExceptionThrow() {

    MigrationExecutor executor = mock(MigrationExecutor.class);
    ChangeLogService changeLogService = mock(ChangeLogService.class);

    SortedSet<ChangeLogItem> changeLogItemList = new TreeSet<>();
    when(changeLogService.fetchChangeLogs()).thenReturn(changeLogItemList);

    EventPublisher eventPublisher = mock(EventPublisher.class);
    new MongockRunnerBase(executor, changeLogService, true, true, eventPublisher)
        .execute();

    ArgumentCaptor<SortedSet> changeLogCaptor = ArgumentCaptor.forClass(SortedSet.class);
    verify(executor).executeMigration(changeLogCaptor.capture());

    Assert.assertEquals(changeLogItemList, changeLogCaptor.getValue());

    verify(eventPublisher, new Times(1)).publishMigrationSuccessEvent(any(MigrationResult.class));
    verify(eventPublisher, new Times(0)).publishMigrationFailedEvent(any());

  }

  @Test
  public void shouldNotBeExecutedNorEventPublished_IfDisabled() {

    MigrationExecutor executor = mock(MigrationExecutor.class);
    ChangeLogService changeLogService = mock(ChangeLogService.class);


    EventPublisher eventPublisher = mock(EventPublisher.class);
    new MongockRunnerBase(executor, changeLogService, true, false, eventPublisher).execute();

    verify(executor, new Times(0)).executeMigration(any());
    verify(eventPublisher, new Times(0)).publishMigrationSuccessEvent(new MigrationResult());
    verify(eventPublisher, new Times(0)).publishMigrationFailedEvent(any());
  }

  @Test(expected = MongockException.class)
  public void shouldPropagateException_IfChangeLogServiceNotValidated() {

    MigrationExecutor executor = mock(MigrationExecutor.class);
    ChangeLogService changeLogService = mock(ChangeLogService.class);

    doThrow(MongockException.class).when(changeLogService).runValidation();

    EventPublisher eventPublisher = mock(EventPublisher.class);
    new MongockRunnerBase(executor, changeLogService, true, true, eventPublisher).execute();
    verify(eventPublisher, new Times(0)).publishMigrationSuccessEvent(new MigrationResult());
    verify(eventPublisher, new Times(1)).publishMigrationFailedEvent(any());

  }

  @Test(expected = MongockException.class)
  public void shouldPropagateException_IfFetchingLogsFails() {

    MigrationExecutor executor = mock(MigrationExecutor.class);
    ChangeLogService changeLogService = mock(ChangeLogService.class);

    doThrow(MongockException.class).when(changeLogService).fetchChangeLogs();

    EventPublisher eventPublisher = mock(EventPublisher.class);
    new MongockRunnerBase(executor, changeLogService, true, true, eventPublisher).execute();
    verify(eventPublisher, new Times(0)).publishMigrationSuccessEvent(new MigrationResult());
    verify(eventPublisher, new Times(1)).publishMigrationFailedEvent(any());

  }

  @Test(expected = MongockException.class)
  public void shouldPropagateException_IfFExecuteMigrationFails() {

    MigrationExecutor executor = mock(MigrationExecutor.class);
    ChangeLogService changeLogService = mock(ChangeLogService.class);

    doThrow(MongockException.class).when(executor).executeMigration(any());

    EventPublisher eventPublisher = mock(EventPublisher.class);
    new MongockRunnerBase(executor, changeLogService, true, true, eventPublisher).execute();
    verify(eventPublisher, new Times(0)).publishMigrationSuccessEvent(new MigrationResult());
    verify(eventPublisher, new Times(1)).publishMigrationFailedEvent(any());

  }


















  @Test(expected = MongockException.class)
  public void shouldPropagateChangockException_EvenWhenThrowExIfCannotLock_IfChangelogServiceNotValidated() {

    MigrationExecutor executor = mock(MigrationExecutor.class);
    ChangeLogService changeLogService = mock(ChangeLogService.class);

    doThrow(MongockException.class).when(changeLogService).runValidation();

    new MongockRunnerBase(executor, changeLogService, false, true, mock(EventPublisher.class)).execute();

  }

  @Test(expected = MongockException.class)
  public void shouldPropagateChangockException_EvenWhenThrowExIfCannotLock_IfFetchFails() {

    MigrationExecutor executor = mock(MigrationExecutor.class);
    ChangeLogService changeLogService = mock(ChangeLogService.class);

    doThrow(MongockException.class).when(changeLogService).fetchChangeLogs();

    new MongockRunnerBase(executor, changeLogService, false, true, mock(EventPublisher.class)).execute();

  }

  @Test(expected = MongockException.class)
  public void shouldPropagateChangockException_EvenWhenThrowExIfCannotLock_IfMigrationExecutionFails() {

    MigrationExecutor executor = mock(MigrationExecutor.class);
    ChangeLogService changeLogService = mock(ChangeLogService.class);

    doThrow(MongockException.class).when(executor).executeMigration(any());

    new MongockRunnerBase(executor, changeLogService, false, true, mock(EventPublisher.class)).execute();

  }

  @Test(expected = MongockException.class)
  public void shouldPropagateLockExceptionWrappedInChangockException_whenExecuteMigrationFails_IfThrowExceptionIfCannotObtainLockTrue() {

    MigrationExecutor executor = mock(MigrationExecutor.class);
    ChangeLogService changeLogService = mock(ChangeLogService.class);

    doThrow(LockCheckException.class).when(executor).executeMigration(any());

    new MongockRunnerBase(executor, changeLogService, true, true, mock(EventPublisher.class)).execute();

  }

  @Test
  public void shouldNotPropagateLockException_whenExecuteMigrationFails_IfThrowExceptionIfCannotObtainLockFalse() {

    MigrationExecutor executor = mock(MigrationExecutor.class);
    ChangeLogService changeLogService = mock(ChangeLogService.class);

    doThrow(LockCheckException.class).when(executor).executeMigration(any());

    new MongockRunnerBase(executor, changeLogService, false, true, mock(EventPublisher.class)).execute();

  }



  @Test(expected = MongockException.class)
  public void shouldWrapGenericExceptionInChangockException() {

    MigrationExecutor executor = mock(MigrationExecutor.class);
    ChangeLogService changeLogService = mock(ChangeLogService.class);

    doThrow(RuntimeException.class).when(executor).executeMigration(any());

    new MongockRunnerBase(executor, changeLogService, false, true, mock(EventPublisher.class)).execute();

  }




  //Events
  @Test
  public void shouldPublishSuccessEvent_whenMigrationSucceed() {

    MigrationExecutor executor = mock(MigrationExecutor.class);
    ChangeLogService changeLogService = mock(ChangeLogService.class);

    SortedSet<ChangeLogItem> changeLogItemList = new TreeSet<>();
    when(changeLogService.fetchChangeLogs()).thenReturn(changeLogItemList);

    EventPublisher eventPublisher = mock(EventPublisher.class);

    new MongockRunnerBase(executor, changeLogService, true, true, eventPublisher)
        .execute();


  }



}
