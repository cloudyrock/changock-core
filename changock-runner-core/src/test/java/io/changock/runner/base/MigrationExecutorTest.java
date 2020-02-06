package io.changock.runner.base;


import io.changock.driver.api.changelog.ChangeLogItem;
import io.changock.driver.api.driver.ChangeSetDependency;
import io.changock.driver.api.driver.ConnectionDriver;
import io.changock.driver.api.entry.ChangeEntry;
import io.changock.driver.api.entry.ChangeEntryService;
import io.changock.driver.api.lock.LockManager;
import io.changock.migration.api.exception.ChangockException;
import io.changock.runner.base.changelogs.executor.test1.ExecutorChangeLog;
import io.changock.runner.base.util.DummyDependencyClass;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
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
  }

  @Test
  public void shouldRunChangeLogsSuccessfully() throws InterruptedException {
    // given
    injectDummyDependency(DummyDependencyClass.class, new DummyDependencyClass());
    when(changeEntryService.isNewChange("newChangeSet", "executor")).thenReturn(true);
    when(changeEntryService.isNewChange("runAlwaysAndNewChangeSet", "executor")).thenReturn(true);
    when(changeEntryService.isNewChange("runAlwaysAndAlreadyExecutedChangeSet", "executor")).thenReturn(false);
    when(changeEntryService.isNewChange("alreadyExecuted", "executor")).thenReturn(false);

    // when
    new MigrationExecutor(driver, new DependencyManager(), 3, 3, 4, new HashMap<>())
        .executeMigration(createInitialChangeLogs(ExecutorChangeLog.class));

    assertTrue("Changelog's methods have not been fully executed", ExecutorChangeLog.latch.await(1, TimeUnit.NANOSECONDS));
    // then
    ArgumentCaptor<ChangeEntry> captor = ArgumentCaptor.forClass(ChangeEntry.class);
    verify(changeEntryService, new Times(3)).save(captor.capture());

    List<ChangeEntry> entries = captor.getAllValues();
    ChangeEntry entry = entries.get(0);
    assertEquals("newChangeSet", entry.getChangeId());
    assertEquals("executor", entry.getAuthor());
    assertEquals(ExecutorChangeLog.class.getName(), entry.getChangeLogClass());
    assertEquals("newChangeSet", entry.getChangeSetMethodName());

    entry = entries.get(1);
    assertEquals("runAlwaysAndNewChangeSet", entry.getChangeId());
    assertEquals("executor", entry.getAuthor());
    assertEquals(ExecutorChangeLog.class.getName(), entry.getChangeLogClass());
    assertEquals("runAlwaysAndNewChangeSet", entry.getChangeSetMethodName());

    entry = entries.get(2);
    assertEquals("runAlwaysAndAlreadyExecutedChangeSet", entry.getChangeId());
    assertEquals("executor", entry.getAuthor());
    assertEquals(ExecutorChangeLog.class.getName(), entry.getChangeLogClass());
    assertEquals("runAlwaysAndAlreadyExecutedChangeSet", entry.getChangeSetMethodName());
  }

  @Test
  public void shouldThrowException_ifNoArgumentFound() {
    // given
    when(changeEntryService.isNewChange("newChangeSet", "executor")).thenReturn(true);

    // then
    exceptionExpected.expect(ChangockException.class);
    exceptionExpected.expectMessage(String.format("Method[%s] using argument[%s] not injected", "newChangeSet", DummyDependencyClass.class.getName()));

    // when
    new MigrationExecutor(driver, new DependencyManager(), 3, 3, 4, new HashMap<>())
        .executeMigration(createInitialChangeLogs(ExecutorChangeLog.class));
  }

  @Test
  public void shouldThrowException_ifWrongArgument() {
    // given
    injectDummyDependency(DummyDependencyClass.class, "Wrong parameter");
    when(changeEntryService.isNewChange("newChangeSet", "executor")).thenReturn(true);

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
    when(changeEntryService.isNewChange("newChangeSet", "executor")).thenReturn(true);

    // when
    try {
      new MigrationExecutor(driver, new DependencyManager(), 3, 3, 4, new HashMap<>())
          .executeMigration(createInitialChangeLogs(ExecutorChangeLog.class));
    } catch (Exception ex) {
    }

    //then
    verify(lockManager, new Times(1)).close();
  }

  private List<ChangeLogItem> createInitialChangeLogs(Class<ExecutorChangeLog> executorChangeLogClass) {
    return new ChangeLogService(Collections.singletonList(executorChangeLogClass.getPackage().getName()), "0", String.valueOf(Integer.MAX_VALUE))
        .fetchChangeLogs();
  }

  private void injectDummyDependency(Class<?> type, Object instance) {
    Set<ChangeSetDependency> dependencies = new HashSet<>();
    dependencies.add(new ChangeSetDependency(type, instance));
    when(driver.getDependencies()).thenReturn(dependencies);
  }

}
