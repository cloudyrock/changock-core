package io.changock.runner.core;

import io.changock.driver.api.changelog.ChangeLogItem;
import io.changock.driver.api.lock.LockCheckException;
import io.changock.migration.api.exception.ChangockException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.internal.verification.Times;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ChangockBaseTest {


  @Test
  public void shouldExecuteSuccessfully() {

    MigrationExecutor executor = mock(MigrationExecutor.class);
    ChangeLogService changeLogService = mock(ChangeLogService.class);

    List<ChangeLogItem> changeLogItemList = new ArrayList<>();
    when(changeLogService.fetchChangeLogs()).thenReturn(changeLogItemList);

    new ChangockBase(executor, changeLogService, true, true)
        .execute();

    ArgumentCaptor<List> changeLogCaptor = ArgumentCaptor.forClass(List.class);
    verify(executor).executeMigration(changeLogCaptor.capture());

    Assert.assertEquals(changeLogItemList, changeLogCaptor.getValue());

  }


  @Test
  public void shouldNotExecute_IfDisabled() {

    MigrationExecutor executor = mock(MigrationExecutor.class);
    ChangeLogService changeLogService = mock(ChangeLogService.class);


    new ChangockBase(executor, changeLogService, true, false).execute();

    verify(executor, new Times(0)).executeMigration(any());

  }

  @Test(expected = ChangockException.class)
  public void shouldPropagateException_IfChangeLogServiceNotValidated() {

    MigrationExecutor executor = mock(MigrationExecutor.class);
    ChangeLogService changeLogService = mock(ChangeLogService.class);

    doThrow(ChangockException.class).when(changeLogService).runValidation();

    new ChangockBase(executor, changeLogService, true, true).execute();

  }

  @Test(expected = ChangockException.class)
  public void shouldPropagateException_IfExecutorNotValidated() {

    MigrationExecutor executor = mock(MigrationExecutor.class);
    ChangeLogService changeLogService = mock(ChangeLogService.class);

    doThrow(ChangockException.class).when(executor).runValidation();

    new ChangockBase(executor, changeLogService, true, true).execute();

  }

  @Test(expected = ChangockException.class)
  public void shouldPropagateException_IfFetchingLogsFails() {

    MigrationExecutor executor = mock(MigrationExecutor.class);
    ChangeLogService changeLogService = mock(ChangeLogService.class);

    doThrow(ChangockException.class).when(changeLogService).fetchChangeLogs();

    new ChangockBase(executor, changeLogService, true, true).execute();

  }

  @Test(expected = ChangockException.class)
  public void shouldPropagateException_IfFExecuteMigrationFails() {

    MigrationExecutor executor = mock(MigrationExecutor.class);
    ChangeLogService changeLogService = mock(ChangeLogService.class);

    doThrow(ChangockException.class).when(executor).executeMigration(any());

    new ChangockBase(executor, changeLogService, true, true).execute();

  }


















  @Test(expected = ChangockException.class)
  public void shouldPropagateChangockException_EvenWhenThrowExIfCannotLock_IfChangelogServiceNotValidated() {

    MigrationExecutor executor = mock(MigrationExecutor.class);
    ChangeLogService changeLogService = mock(ChangeLogService.class);

    doThrow(ChangockException.class).when(changeLogService).runValidation();

    new ChangockBase(executor, changeLogService, false, true).execute();

  }

  @Test(expected = ChangockException.class)
  public void shouldPropagateChangockException_EvenWhenThrowExIfCannotLock_IfExecutorNotValidated() {

    MigrationExecutor executor = mock(MigrationExecutor.class);
    ChangeLogService changeLogService = mock(ChangeLogService.class);

    doThrow(ChangockException.class).when(executor).runValidation();

    new ChangockBase(executor, changeLogService, false, true).execute();

  }

  @Test(expected = ChangockException.class)
  public void shouldPropagateChangockException_EvenWhenThrowExIfCannotLock_IfFetchFails() {

    MigrationExecutor executor = mock(MigrationExecutor.class);
    ChangeLogService changeLogService = mock(ChangeLogService.class);

    doThrow(ChangockException.class).when(changeLogService).fetchChangeLogs();

    new ChangockBase(executor, changeLogService, false, true).execute();

  }

  @Test(expected = ChangockException.class)
  public void shouldPropagateChangockException_EvenWhenThrowExIfCannotLock_IfMigrationExecutionFails() {

    MigrationExecutor executor = mock(MigrationExecutor.class);
    ChangeLogService changeLogService = mock(ChangeLogService.class);

    doThrow(ChangockException.class).when(executor).executeMigration(any());

    new ChangockBase(executor, changeLogService, false, true).execute();

  }

  @Test(expected = ChangockException.class)
  public void shouldPropagateLockExceptionWrappedInChangockException_whenExecuteMigrationFails_IfThrowExceptionIfCannotObtainLockTrue() {

    MigrationExecutor executor = mock(MigrationExecutor.class);
    ChangeLogService changeLogService = mock(ChangeLogService.class);

    doThrow(LockCheckException.class).when(executor).executeMigration(any());

    new ChangockBase(executor, changeLogService, true, true).execute();

  }

  @Test
  public void shouldNotPropagateLockException_whenExecuteMigrationFails_IfThrowExceptionIfCannotObtainLockFalse() {

    MigrationExecutor executor = mock(MigrationExecutor.class);
    ChangeLogService changeLogService = mock(ChangeLogService.class);

    doThrow(LockCheckException.class).when(executor).executeMigration(any());

    new ChangockBase(executor, changeLogService, false, true).execute();

  }



  @Test(expected = ChangockException.class)
  public void shouldWrapGenericExceptionInChangockException() {

    MigrationExecutor executor = mock(MigrationExecutor.class);
    ChangeLogService changeLogService = mock(ChangeLogService.class);

    doThrow(RuntimeException.class).when(executor).executeMigration(any());

    new ChangockBase(executor, changeLogService, false, true).execute();

  }

}
