package com.github.cloudyrock.mongock.runner.core.executor;

import com.github.cloudyrock.mongock.ChangeLogItem;
import com.github.cloudyrock.mongock.config.MongockConfiguration;
import com.github.cloudyrock.mongock.config.executor.ChangeExecutorConfiguration;
import com.github.cloudyrock.mongock.driver.api.driver.ConnectionDriver;
import com.github.cloudyrock.mongock.driver.api.lock.LockCheckException;
import com.github.cloudyrock.mongock.exception.MongockException;
import com.github.cloudyrock.mongock.runner.core.event.EventPublisher;
import com.github.cloudyrock.mongock.runner.core.event.result.MigrationResult;
import com.github.cloudyrock.mongock.runner.core.executor.changelog.ChangeLogService;
import com.github.cloudyrock.mongock.runner.core.executor.dependency.DependencyManager;
import com.github.cloudyrock.mongock.runner.core.executor.operation.change.MigrationExecutor;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.internal.verification.Times;

import java.lang.reflect.Parameter;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MongockRunnerTest {

  @Rule
  public ExpectedException exceptionExpected = ExpectedException.none();

  @Test
  public void shouldNotBeExecutedNorEventPublished_IfDisabled() {

    Executor executor = mock(MigrationExecutor.class);
    ChangeLogService changeLogService = mock(ChangeLogService.class);


    EventPublisher eventPublisher = mock(EventPublisher.class);
    new MongockRunnerImpl(executor, true, false, eventPublisher).execute();

    verify(executor, new Times(0))
        .executeMigration();
//    .executeMigration(any());
    verify(eventPublisher, new Times(0)).publishMigrationSuccessEvent(MigrationResult.successResult());
    verify(eventPublisher, new Times(0)).publishMigrationFailedEvent(any());
  }



  @Test(expected = MongockException.class)
  public void shouldPropagateException_IfFExecuteMigrationFails() {

    Executor executor = mock(MigrationExecutor.class);
    ChangeLogService changeLogService = mock(ChangeLogService.class);

    doThrow(MongockException.class).when(executor)
        .executeMigration();
//    .executeMigration(any());

    EventPublisher eventPublisher = mock(EventPublisher.class);
    new MongockRunnerImpl(executor, true, true, eventPublisher).execute();
    verify(eventPublisher, new Times(0)).publishMigrationSuccessEvent(MigrationResult.successResult());
    verify(eventPublisher, new Times(1)).publishMigrationFailedEvent(any());

  }


  @Test(expected = MongockException.class)
  public void shouldPropagateMongockException_EvenWhenThrowExIfCannotLock_IfMigrationExecutionFails() {

    Executor executor = mock(MigrationExecutor.class);
    ChangeLogService changeLogService = mock(ChangeLogService.class);

    doThrow(MongockException.class).when(executor)
        .executeMigration();
//    .executeMigration(any());

    new MongockRunnerImpl(executor, false, true, mock(EventPublisher.class)).execute();

  }

  @Test(expected = MongockException.class)
  public void shouldPropagateLockExceptionWrappedInMongockException_whenExecuteMigrationFails_IfThrowExceptionIfCannotObtainLockTrue() {

    Executor executor = mock(MigrationExecutor.class);
    ChangeLogService changeLogService = mock(ChangeLogService.class);

    doThrow(LockCheckException.class).when(executor)
        .executeMigration();
//        .executeMigration(any());

    new MongockRunnerImpl(executor, true, true, mock(EventPublisher.class)).execute();

  }

  @Test
  public void shouldNotPropagateLockException_whenExecuteMigrationFails_IfThrowExceptionIfCannotObtainLockFalse() {

    Executor executor = mock(MigrationExecutor.class);
    ChangeLogService changeLogService = mock(ChangeLogService.class);

    doThrow(LockCheckException.class).when(executor)
        .executeMigration();
//    .executeMigration(any());

    new MongockRunnerImpl(executor, false, true, mock(EventPublisher.class)).execute();

  }



  @Test(expected = MongockException.class)
  public void shouldWrapGenericExceptionInMongockException() {

    Executor executor = mock(MigrationExecutor.class);
    ChangeLogService changeLogService = mock(ChangeLogService.class);

    doThrow(RuntimeException.class).when(executor)
        .executeMigration();

    new MongockRunnerImpl(executor, false, true, mock(EventPublisher.class)).execute();

  }


  //Events
  @Test
  public void shouldPublishSuccessEvent_whenMigrationSucceed() {

    Executor executor = mock(MigrationExecutor.class);
    ChangeLogService changeLogService = mock(ChangeLogService.class);

    SortedSet<ChangeLogItem> changeLogItemList = new TreeSet<>();
    when(changeLogService.fetchChangeLogs()).thenReturn(changeLogItemList);

    EventPublisher eventPublisher = mock(EventPublisher.class);

    new MongockRunnerImpl(executor, true, true, eventPublisher)
        .execute();
  }


  @Test
  public void shouldPropagateMongockException_EvenWhenThrowExIfCannotLock_IfChangelogServiceNotValidated() {

    Executor executor = new Executor() {
      @Override
      public Object executeMigration() {
        throw new LockCheckException("Cannot obtain lock");
      }

      @Override
      public boolean isExecutionInProgress() {
        return false;
      }
    };


    exceptionExpected.expect(MongockException.class);
    exceptionExpected.expectMessage("Cannot obtain lock");

    new MongockRunnerImpl(executor, true, true, mock(EventPublisher.class)).execute();

  }


}
