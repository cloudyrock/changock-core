package io.changock.runner.core;


import io.changock.driver.api.driver.ForbiddenParametersMap;
import io.changock.driver.api.entry.ChangeState;
import io.changock.migration.api.ChangeLogItem;
import io.changock.driver.api.driver.ChangeSetDependency;
import io.changock.driver.api.driver.ConnectionDriver;
import io.changock.driver.api.entry.ChangeEntry;
import io.changock.driver.api.entry.ChangeEntryService;
import io.changock.driver.api.lock.LockManager;
import io.changock.migration.api.exception.ChangockException;
import io.changock.runner.core.changelogs.executor.test1.ExecutorChangeLog;
import io.changock.runner.core.changelogs.executor.test3_with_nonFailFast.ExecutorWithNonFailFastChangeLog;
import io.changock.runner.core.changelogs.executor.test4_with_failfast.ExecutorWithFailFastChangeLog;
import io.changock.runner.core.changelogs.withForbiddenParameter.ChangeLogWithForbiddenParameter;
import io.changock.runner.core.changelogs.withForbiddenParameter.ForbiddenParameter;
import io.changock.runner.core.util.DummyDependencyClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.internal.verification.Times;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MigrationExecutorTest {

  private ChangeEntryService changeEntryService;
  private LockManager lockManager;
  private ConnectionDriver driver;

  @Rule
  public ExpectedException exceptionExpected = ExpectedException.none();

  @Before
  public void setUp() {
    lockManager = mock(LockManager.class);
    changeEntryService = mock(ChangeEntryService.class);
    driver = mock(ConnectionDriver.class);
    when(driver.getLockManager()).thenReturn(lockManager);
    when(driver.getLockManager()).thenReturn(lockManager);
    when(driver.getChangeEntryService()).thenReturn(changeEntryService);
    ForbiddenParametersMap forbiddenParameters = new ForbiddenParametersMap();
    forbiddenParameters.put(ForbiddenParameter.class, String.class);
    when(driver.getForbiddenParameters()).thenReturn(forbiddenParameters);

  }

  @Test
  public void shouldRunChangeLogsSuccessfully() throws InterruptedException {
    // given
    injectDummyDependency(DummyDependencyClass.class, new DummyDependencyClass());
    when(changeEntryService.isAlreadyExecuted("newChangeSet", "executor")).thenReturn(false);
    when(changeEntryService.isAlreadyExecuted("runAlwaysAndNewChangeSet", "executor")).thenReturn(false);
    when(changeEntryService.isAlreadyExecuted("runAlwaysAndAlreadyExecutedChangeSet", "executor")).thenReturn(true);
    when(changeEntryService.isAlreadyExecuted("alreadyExecuted", "executor")).thenReturn(true);

    // when
    new MigrationExecutor(driver, new DependencyManager(), 3, 3, 4, new HashMap<>())
        .executeMigration(createInitialChangeLogs(ExecutorChangeLog.class));

    assertTrue("Changelog's methods have not been fully executed", ExecutorChangeLog.latch.await(1, TimeUnit.NANOSECONDS));
    // then
    ArgumentCaptor<ChangeEntry> captor = ArgumentCaptor.forClass(ChangeEntry.class);
    verify(changeEntryService, new Times(4)).save(captor.capture());

    List<ChangeEntry> entries = captor.getAllValues();
    assertEquals(4, entries.size());
    ChangeEntry entry = entries.get(0);
    assertEquals("newChangeSet", entry.getChangeId());
    assertEquals("executor", entry.getAuthor());
    assertEquals(ExecutorChangeLog.class.getName(), entry.getChangeLogClass());
    assertEquals("newChangeSet", entry.getChangeSetMethodName());
    assertEquals(ChangeState.EXECUTED, entry.getState());

    entry = entries.get(1);
    assertEquals("runAlwaysAndNewChangeSet", entry.getChangeId());
    assertEquals("executor", entry.getAuthor());
    assertEquals(ExecutorChangeLog.class.getName(), entry.getChangeLogClass());
    assertEquals("runAlwaysAndNewChangeSet", entry.getChangeSetMethodName());
    assertEquals(ChangeState.EXECUTED, entry.getState());

    entry = entries.get(2);
    assertEquals("alreadyExecuted", entry.getChangeId());
    assertEquals("executor", entry.getAuthor());
    assertEquals(ExecutorChangeLog.class.getName(), entry.getChangeLogClass());
    assertEquals("alreadyExecuted", entry.getChangeSetMethodName());
    assertEquals(ChangeState.IGNORED, entry.getState());

    entry = entries.get(3);
    assertEquals("runAlwaysAndAlreadyExecutedChangeSet", entry.getChangeId());
    assertEquals("executor", entry.getAuthor());
    assertEquals(ExecutorChangeLog.class.getName(), entry.getChangeLogClass());
    assertEquals("runAlwaysAndAlreadyExecutedChangeSet", entry.getChangeSetMethodName());
    assertEquals(ChangeState.EXECUTED, entry.getState());
  }


  @Test
  public void shouldAbortMigrationButSaveFailedChangeSet_IfChangeSetThrowsException() throws InterruptedException {
    // given
    injectDummyDependency(DummyDependencyClass.class, new DummyDependencyClass());
    when(changeEntryService.isAlreadyExecuted("newChangeSet", "executor")).thenReturn(false);
    when(changeEntryService.isAlreadyExecuted("runAlwaysAndNewChangeSet", "executor")).thenReturn(false);
    when(changeEntryService.isAlreadyExecuted("runAlwaysAndAlreadyExecutedChangeSet", "executor")).thenReturn(true);
    when(changeEntryService.isAlreadyExecuted("throwsException", "executor")).thenReturn(false);

    // when
    try{
      new MigrationExecutor(driver, new DependencyManager(), 3, 3, 4, new HashMap<>())
          .executeMigration(createInitialChangeLogs(ExecutorWithFailFastChangeLog.class));
    } catch (Exception ex) {
      //ignored
    }

    assertTrue("Changelog's methods have not been fully executed", ExecutorWithFailFastChangeLog.latch.await(1, TimeUnit.NANOSECONDS));
    // then
    ArgumentCaptor<ChangeEntry> captor = ArgumentCaptor.forClass(ChangeEntry.class);
    verify(changeEntryService, new Times(3)).save(captor.capture());

    List<ChangeEntry> entries = captor.getAllValues();
    assertEquals(3, entries.size());
    ChangeEntry entry = entries.get(0);
    assertEquals("newChangeSet", entry.getChangeId());
    assertEquals("executor", entry.getAuthor());
    assertEquals(ExecutorWithFailFastChangeLog.class.getName(), entry.getChangeLogClass());
    assertEquals("newChangeSet", entry.getChangeSetMethodName());
    assertEquals(ChangeState.EXECUTED, entry.getState());

    entry = entries.get(1);
    assertEquals("runAlwaysAndNewChangeSet", entry.getChangeId());
    assertEquals("executor", entry.getAuthor());
    assertEquals(ExecutorWithFailFastChangeLog.class.getName(), entry.getChangeLogClass());
    assertEquals("runAlwaysAndNewChangeSet", entry.getChangeSetMethodName());
    assertEquals(ChangeState.EXECUTED, entry.getState());

    entry = entries.get(2);
    assertEquals("throwsException", entry.getChangeId());
    assertEquals("executor", entry.getAuthor());
    assertEquals(ExecutorWithFailFastChangeLog.class.getName(), entry.getChangeLogClass());
    assertEquals("throwsException", entry.getChangeSetMethodName());
    assertEquals(ChangeState.FAILED, entry.getState());
  }

  @Test
  public void shouldThrowException_ifNoArgumentFound() {
    // given
    when(changeEntryService.isAlreadyExecuted("newChangeSet", "executor")).thenReturn(false);

    // then
    exceptionExpected.expect(ChangockException.class);
    exceptionExpected.expectMessage("Error in method[ExecutorChangeLog.newChangeSet] : Wrong parameter[DummyDependencyClass]");

    // when
    new MigrationExecutor(driver, new DependencyManager(), 3, 3, 4, new HashMap<>())
        .executeMigration(createInitialChangeLogs(ExecutorChangeLog.class));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void shouldThrowException_ifWrongArgument() {
    // given
    injectDummyDependency(DummyDependencyClass.class, "Wrong parameter");
    when(changeEntryService.isAlreadyExecuted("newChangeSet", "executor")).thenReturn(false);

    //then
    exceptionExpected.expect(ChangockException.class);
    exceptionExpected.expectMessage("argument type mismatch");

    // when
    new MigrationExecutor(driver, new DependencyManager(), 3, 3, 4, new HashMap<>())
        .executeMigration(createInitialChangeLogs(ExecutorChangeLog.class));
  }

  @Test
  public void shouldCloseLockManager_WhenException() {
    // given
    injectDummyDependency(DummyDependencyClass.class, "Wrong parameter");
    when(changeEntryService.isAlreadyExecuted("newChangeSet", "executor")).thenReturn(false);

    // when
    try {
      new MigrationExecutor(driver, new DependencyManager(), 3, 3, 4, new HashMap<>())
          .executeMigration(createInitialChangeLogs(ExecutorChangeLog.class));
    } catch (Exception ex) {
    }

    //then
    verify(lockManager, new Times(1)).close();
  }


  @Test(expected = ChangockException.class)
  public void shouldPropagateChangockException_EvenWhenThrowExIfCannotLock_IfDriverNotValidated() {
    // given
    injectDummyDependency(DummyDependencyClass.class, "Wrong parameter");
    when(changeEntryService.isAlreadyExecuted("newChangeSet", "executor")).thenReturn(false);
    doThrow(ChangockException.class).when(driver).runValidation();

    // when
    new MigrationExecutor(driver, new DependencyManager(), 3, 3, 4, new HashMap<>())
        .executeMigration(createInitialChangeLogs(ExecutorChangeLog.class));
  }

  @Test
  public void shouldContinueMigration_whenAChangeSetFails_ifItIsNonFailFast() throws InterruptedException {
    // given
    injectDummyDependency(DummyDependencyClass.class, new DummyDependencyClass());
    when(changeEntryService.isAlreadyExecuted("newChangeSet1", "executor")).thenReturn(false);
    when(changeEntryService.isAlreadyExecuted("changeSetNonFailFast", "executor")).thenReturn(false);
    when(changeEntryService.isAlreadyExecuted("newChangeSet2", "executor")).thenReturn(false);

    // when
    new MigrationExecutor(driver, new DependencyManager(), 3, 3, 4, new HashMap<>())
        .executeMigration(createInitialChangeLogs(ExecutorWithNonFailFastChangeLog.class));

    assertTrue("Changelog's methods have not been fully executed", ExecutorWithNonFailFastChangeLog.latch.await(1, TimeUnit.NANOSECONDS));
    // then
    ArgumentCaptor<ChangeEntry> captor = ArgumentCaptor.forClass(ChangeEntry.class);
    verify(changeEntryService, new Times(3)).save(captor.capture());

    List<ChangeEntry> entries = captor.getAllValues();
    assertEquals(3, entries.size());

    ChangeEntry entry = entries.get(0);
    assertEquals("newChangeSet1", entry.getChangeId());
    assertEquals("executor", entry.getAuthor());
    assertEquals(ExecutorWithNonFailFastChangeLog.class.getName(), entry.getChangeLogClass());
    assertEquals("newChangeSet1", entry.getChangeSetMethodName());
    assertEquals(ChangeState.EXECUTED, entry.getState());


    entry = entries.get(1);
    assertEquals("changeSetNonFailFast", entry.getChangeId());
    assertEquals("executor", entry.getAuthor());
    assertEquals(ExecutorWithNonFailFastChangeLog.class.getName(), entry.getChangeLogClass());
    assertEquals("changeSetNonFailFast", entry.getChangeSetMethodName());
    assertEquals(ChangeState.FAILED, entry.getState());

    entry = entries.get(2);
    assertEquals("newChangeSet2", entry.getChangeId());
    assertEquals("executor", entry.getAuthor());
    assertEquals(ExecutorWithNonFailFastChangeLog.class.getName(), entry.getChangeLogClass());
    assertEquals("newChangeSet2", entry.getChangeSetMethodName());
    assertEquals(ChangeState.EXECUTED, entry.getState());
  }




  @Test
  public void shouldFail_whenRunningChangeSet_ifForbiddenParameterFromDriver() {

    when(changeEntryService.isAlreadyExecuted("withForbiddenParameter", "executor")).thenReturn(true);

    // then
    exceptionExpected.expect(ChangockException.class);
    exceptionExpected.expectMessage("Error in method[ChangeLogWithForbiddenParameter.withForbiddenParameter] : Forbidden parameter[ForbiddenParameter]. Must be replaced with [String]");

    // when
    new MigrationExecutor(driver, new DependencyManager(), 3, 3, 4, new HashMap<>())
        .executeMigration(createInitialChangeLogs(ChangeLogWithForbiddenParameter.class));
  }

  private List<ChangeLogItem> createInitialChangeLogs(Class<?> executorChangeLogClass) {
    return new ChangeLogService(Collections.singletonList(executorChangeLogClass.getPackage().getName()), "0", String.valueOf(Integer.MAX_VALUE))
        .fetchChangeLogs();
  }

  private void injectDummyDependency(Class<?> type, Object instance) {
    Set<ChangeSetDependency> dependencies = new HashSet<>();
    dependencies.add(new ChangeSetDependency(type, instance));
    when(driver.getDependencies()).thenReturn(dependencies);
  }
}
