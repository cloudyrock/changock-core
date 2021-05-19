package com.github.cloudyrock.mongock.runner.core.executor;

import com.github.cloudyrock.mongock.ChangeLogItem;
import com.github.cloudyrock.mongock.driver.api.lock.LockCheckException;
import com.github.cloudyrock.mongock.exception.MongockException;
import com.github.cloudyrock.mongock.runner.core.event.EventPublisher;
import com.github.cloudyrock.mongock.runner.core.event.MigrationResult;
import com.github.cloudyrock.mongock.runner.core.executor.changelog.ChangeLogService;
import com.github.cloudyrock.mongock.runner.core.executor.operation.change.MigrationExecutor;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.internal.verification.Times;

import java.util.SortedSet;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MongockRunnerTest {


  @Test
  public void shouldExecuteAllTheChangeLogsAndPublishRightEvent_whenNoExceptionThrow() {

    Executor executor = mock(MigrationExecutor.class);
    ChangeLogService changeLogService = mock(ChangeLogService.class);

    SortedSet<ChangeLogItem> changeLogItemList = new TreeSet<>();
    when(changeLogService.fetchChangeLogs()).thenReturn(changeLogItemList);

    EventPublisher eventPublisher = mock(EventPublisher.class);
    new MongockRunner(executor, changeLogService, true, true, eventPublisher)
        .execute();

    ArgumentCaptor<SortedSet> changeLogCaptor = ArgumentCaptor.forClass(SortedSet.class);
    verify(executor).executeMigration(changeLogCaptor.capture());

    assertEquals(changeLogItemList, changeLogCaptor.getValue());

    verify(eventPublisher, new Times(1)).publishMigrationSuccessEvent(any(MigrationResult.class));
    verify(eventPublisher, new Times(0)).publishMigrationFailedEvent(any());

  }

  @Test
  public void shouldNotBeExecutedNorEventPublished_IfDisabled() {

    Executor executor = mock(MigrationExecutor.class);
    ChangeLogService changeLogService = mock(ChangeLogService.class);


    EventPublisher eventPublisher = mock(EventPublisher.class);
    new MongockRunner(executor, changeLogService, true, false, eventPublisher).execute();

    verify(executor, new Times(0)).executeMigration(any());
    verify(eventPublisher, new Times(0)).publishMigrationSuccessEvent(new MigrationResult());
    verify(eventPublisher, new Times(0)).publishMigrationFailedEvent(any());
  }

  @Test(expected = MongockException.class)
  public void shouldPropagateException_IfChangeLogServiceNotValidated() {

    Executor executor = mock(MigrationExecutor.class);
    ChangeLogService changeLogService = mock(ChangeLogService.class);

    doThrow(MongockException.class).when(changeLogService).runValidation();

    EventPublisher eventPublisher = mock(EventPublisher.class);
    new MongockRunner(executor, changeLogService, true, true, eventPublisher).execute();
    verify(eventPublisher, new Times(0)).publishMigrationSuccessEvent(new MigrationResult());
    verify(eventPublisher, new Times(1)).publishMigrationFailedEvent(any());

  }

  @Test(expected = MongockException.class)
  public void shouldPropagateException_IfFetchingLogsFails() {

    Executor executor = mock(MigrationExecutor.class);
    ChangeLogService changeLogService = mock(ChangeLogService.class);

    doThrow(MongockException.class).when(changeLogService).fetchChangeLogs();

    EventPublisher eventPublisher = mock(EventPublisher.class);
    new MongockRunner(executor, changeLogService, true, true, eventPublisher).execute();
    verify(eventPublisher, new Times(0)).publishMigrationSuccessEvent(new MigrationResult());
    verify(eventPublisher, new Times(1)).publishMigrationFailedEvent(any());

  }

  @Test(expected = MongockException.class)
  public void shouldPropagateException_IfFExecuteMigrationFails() {

    Executor executor = mock(MigrationExecutor.class);
    ChangeLogService changeLogService = mock(ChangeLogService.class);

    doThrow(MongockException.class).when(executor).executeMigration(any());

    EventPublisher eventPublisher = mock(EventPublisher.class);
    new MongockRunner(executor, changeLogService, true, true, eventPublisher).execute();
    verify(eventPublisher, new Times(0)).publishMigrationSuccessEvent(new MigrationResult());
    verify(eventPublisher, new Times(1)).publishMigrationFailedEvent(any());

  }


















  @Test(expected = MongockException.class)
  public void shouldPropagateMongockException_EvenWhenThrowExIfCannotLock_IfChangelogServiceNotValidated() {

    Executor executor = mock(MigrationExecutor.class);
    ChangeLogService changeLogService = mock(ChangeLogService.class);

    doThrow(MongockException.class).when(changeLogService).runValidation();

    new MongockRunner(executor, changeLogService, false, true, mock(EventPublisher.class)).execute();

  }

  @Test(expected = MongockException.class)
  public void shouldPropagateMongockException_EvenWhenThrowExIfCannotLock_IfFetchFails() {

    Executor executor = mock(MigrationExecutor.class);
    ChangeLogService changeLogService = mock(ChangeLogService.class);

    doThrow(MongockException.class).when(changeLogService).fetchChangeLogs();

    new MongockRunner(executor, changeLogService, false, true, mock(EventPublisher.class)).execute();

  }

  @Test(expected = MongockException.class)
  public void shouldPropagateMongockException_EvenWhenThrowExIfCannotLock_IfMigrationExecutionFails() {

    Executor executor = mock(MigrationExecutor.class);
    ChangeLogService changeLogService = mock(ChangeLogService.class);

    doThrow(MongockException.class).when(executor).executeMigration(any());

    new MongockRunner(executor, changeLogService, false, true, mock(EventPublisher.class)).execute();

  }

  @Test(expected = MongockException.class)
  public void shouldPropagateLockExceptionWrappedInMongockException_whenExecuteMigrationFails_IfThrowExceptionIfCannotObtainLockTrue() {

    Executor executor = mock(MigrationExecutor.class);
    ChangeLogService changeLogService = mock(ChangeLogService.class);

    doThrow(LockCheckException.class).when(executor).executeMigration(any());

    new MongockRunner(executor, changeLogService, true, true, mock(EventPublisher.class)).execute();

  }

  @Test
  public void shouldNotPropagateLockException_whenExecuteMigrationFails_IfThrowExceptionIfCannotObtainLockFalse() {

    Executor executor = mock(MigrationExecutor.class);
    ChangeLogService changeLogService = mock(ChangeLogService.class);

    doThrow(LockCheckException.class).when(executor).executeMigration(any());

    new MongockRunner(executor, changeLogService, false, true, mock(EventPublisher.class)).execute();

  }



  @Test(expected = MongockException.class)
  public void shouldWrapGenericExceptionInMongockException() {

    Executor executor = mock(MigrationExecutor.class);
    ChangeLogService changeLogService = mock(ChangeLogService.class);

    doThrow(RuntimeException.class).when(executor).executeMigration(any());

    new MongockRunner(executor, changeLogService, false, true, mock(EventPublisher.class)).execute();

  }




  //Events
  @Test
  public void shouldPublishSuccessEvent_whenMigrationSucceed() {

    Executor executor = mock(MigrationExecutor.class);
    ChangeLogService changeLogService = mock(ChangeLogService.class);

    SortedSet<ChangeLogItem> changeLogItemList = new TreeSet<>();
    when(changeLogService.fetchChangeLogs()).thenReturn(changeLogItemList);

    EventPublisher eventPublisher = mock(EventPublisher.class);

    new MongockRunner(executor, changeLogService, true, true, eventPublisher)
        .execute();


  }



}
