package io.changock.runner.core.executor;


import io.changock.driver.api.driver.ChangeSetDependency;
import io.changock.driver.api.driver.ConnectionDriver;
import io.changock.driver.api.driver.ForbiddenParametersMap;
import io.changock.driver.api.entry.ChangeEntry;
import io.changock.driver.api.entry.ChangeEntryService;
import io.changock.driver.api.entry.ChangeState;
import io.changock.driver.api.lock.LockManager;
import io.changock.driver.api.lock.guard.proxy.LockGuardProxyFactory;
import com.github.cloudyrock.mongock.ChangeLogItem;
import com.github.cloudyrock.mongock.exception.MongockException;
import com.github.cloudyrock.mongock.config.LegacyMigration;
import com.github.cloudyrock.mongock.config.LegacyMigrationMappingFields;
import io.changock.runner.core.changelogs.executor.test1.ExecutorChangeLog;
import io.changock.runner.core.changelogs.executor.test3_with_nonFailFast.ExecutorWithNonFailFastChangeLog;
import io.changock.runner.core.changelogs.executor.test4_with_failfast.ExecutorWithFailFastChangeLog;
import io.changock.runner.core.changelogs.executor.withInterfaceParameter.ChangeLogWithInterfaceParameter;
import io.changock.runner.core.changelogs.legacymigration.LegacyMigrationChangeLog;
import io.changock.runner.core.changelogs.skipmigration.alreadyexecuted.ChangeLogAlreadyExecuted;
import io.changock.runner.core.changelogs.skipmigration.runalways.ChangeLogAlreadyExecutedRunAlways;
import io.changock.runner.core.changelogs.withForbiddenParameter.ChangeLogWithForbiddenParameter;
import io.changock.runner.core.changelogs.withForbiddenParameter.ForbiddenParameter;
import io.changock.runner.core.changelogs.skipmigration.withnochangeset.ChangeLogWithNoChangeSet;
import io.changock.runner.core.util.DummyDependencyClass;
import io.changock.runner.core.util.InterfaceDependencyImpl;
import io.changock.runner.core.util.InterfaceDependencyImplNoLockGarded;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.internal.verification.Times;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
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
    runChangeLogsTest(false);
  }


  @Test
  public void shouldTrackIgnored_IfFlagTrackIgnored() throws InterruptedException {
    runChangeLogsTest(true);
  }


  @SuppressWarnings("unchecked")
  private void runChangeLogsTest(boolean trackingIgnored) throws InterruptedException {

    // given
    injectDummyDependency(DummyDependencyClass.class, new DummyDependencyClass());
    when(changeEntryService.isAlreadyExecuted("newChangeSet", "executor")).thenReturn(false);
    when(changeEntryService.isAlreadyExecuted("runAlwaysAndNewChangeSet", "executor")).thenReturn(false);
    when(changeEntryService.isAlreadyExecuted("runAlwaysAndAlreadyExecutedChangeSet", "executor")).thenReturn(true);
    when(changeEntryService.isAlreadyExecuted("alreadyExecuted", "executor")).thenReturn(true);

    // when
    new MigrationExecutor(driver, new DependencyManager(), getMigrationConfig(trackingIgnored), new HashMap<>())
        .executeMigration(createInitialChangeLogs(ExecutorChangeLog.class));

    assertTrue("Changelog's methods have not been fully executed", ExecutorChangeLog.latch.await(1, TimeUnit.NANOSECONDS));
    // then
    ArgumentCaptor<ChangeEntry> captor = ArgumentCaptor.forClass(ChangeEntry.class);
    verify(changeEntryService, new Times(trackingIgnored ? 4 : 3)).save(captor.capture());

    List<ChangeEntry> entries = captor.getAllValues();
    assertEquals(trackingIgnored ? 4 : 3, entries.size());
    ChangeEntry entry = entries.get(0);
    assertEquals("newChangeSet", entry.getChangeId());
    assertEquals("executor", entry.getAuthor());
    assertEquals(ExecutorChangeLog.class.getName(), entry.getChangeLogClass());
    assertEquals("newChangeSet", entry.getChangeSetMethod());
    assertEquals(ChangeState.EXECUTED, entry.getState());

    entry = entries.get(1);
    assertEquals("runAlwaysAndNewChangeSet", entry.getChangeId());
    assertEquals("executor", entry.getAuthor());
    assertEquals(ExecutorChangeLog.class.getName(), entry.getChangeLogClass());
    assertEquals("runAlwaysAndNewChangeSet", entry.getChangeSetMethod());
    assertEquals(ChangeState.EXECUTED, entry.getState());

    int nextIndex = 2;
    if(trackingIgnored) {
      entry = entries.get(nextIndex);
      assertEquals("alreadyExecuted", entry.getChangeId());
      assertEquals("executor", entry.getAuthor());
      assertEquals(ExecutorChangeLog.class.getName(), entry.getChangeLogClass());
      assertEquals("alreadyExecuted", entry.getChangeSetMethod());
      assertEquals(ChangeState.IGNORED, entry.getState());
      nextIndex++;
    }

    entry = entries.get(nextIndex);
    assertEquals("runAlwaysAndAlreadyExecutedChangeSet", entry.getChangeId());
    assertEquals("executor", entry.getAuthor());
    assertEquals(ExecutorChangeLog.class.getName(), entry.getChangeLogClass());
    assertEquals("runAlwaysAndAlreadyExecutedChangeSet", entry.getChangeSetMethod());
    assertEquals(ChangeState.EXECUTED, entry.getState());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void shouldAbortMigrationButSaveFailedChangeSet_IfChangeSetThrowsException() throws InterruptedException {
    // given
    injectDummyDependency(DummyDependencyClass.class, new DummyDependencyClass());
    when(changeEntryService.isAlreadyExecuted("newChangeSet", "executor")).thenReturn(false);
    when(changeEntryService.isAlreadyExecuted("runAlwaysAndNewChangeSet", "executor")).thenReturn(false);
    when(changeEntryService.isAlreadyExecuted("runAlwaysAndAlreadyExecutedChangeSet", "executor")).thenReturn(true);
    when(changeEntryService.isAlreadyExecuted("throwsException", "executor")).thenReturn(false);

    // when
    try {
      new MigrationExecutor(driver, new DependencyManager(), getMigrationConfig(), new HashMap<>())
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
    assertEquals("newChangeSet", entry.getChangeSetMethod());
    assertEquals(ChangeState.EXECUTED, entry.getState());

    entry = entries.get(1);
    assertEquals("runAlwaysAndNewChangeSet", entry.getChangeId());
    assertEquals("executor", entry.getAuthor());
    assertEquals(ExecutorWithFailFastChangeLog.class.getName(), entry.getChangeLogClass());
    assertEquals("runAlwaysAndNewChangeSet", entry.getChangeSetMethod());
    assertEquals(ChangeState.EXECUTED, entry.getState());

    entry = entries.get(2);
    assertEquals("throwsException", entry.getChangeId());
    assertEquals("executor", entry.getAuthor());
    assertEquals(ExecutorWithFailFastChangeLog.class.getName(), entry.getChangeLogClass());
    assertEquals("throwsException", entry.getChangeSetMethod());
    assertEquals(ChangeState.FAILED, entry.getState());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void shouldThrowException_ifNoArgumentFound() {
    // given
    when(changeEntryService.isAlreadyExecuted("newChangeSet", "executor")).thenReturn(false);

    // then
    exceptionExpected.expect(MongockException.class);
    exceptionExpected.expectMessage("Error in method[ExecutorChangeLog.newChangeSet] : Wrong parameter[DummyDependencyClass]");

    // when
    new MigrationExecutor(driver, new DependencyManager(), getMigrationConfig(), new HashMap<>())
        .executeMigration(createInitialChangeLogs(ExecutorChangeLog.class));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void shouldThrowException_ifWrongArgument() {
    // given
    injectDummyDependency(DummyDependencyClass.class, "Wrong parameter");
    when(changeEntryService.isAlreadyExecuted("newChangeSet", "executor")).thenReturn(false);

    //then
    exceptionExpected.expect(MongockException.class);
    exceptionExpected.expectMessage("argument type mismatch");

    // when
    new MigrationExecutor(driver, new DependencyManager(), getMigrationConfig(), new HashMap<>())
        .executeMigration(createInitialChangeLogs(ExecutorChangeLog.class));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void shouldCloseLockManager_WhenException() {
    // given
    injectDummyDependency(DummyDependencyClass.class, "Wrong parameter");
    when(changeEntryService.isAlreadyExecuted("newChangeSet", "executor")).thenReturn(false);

    // when
    try {
      new MigrationExecutor(driver, new DependencyManager(), getMigrationConfig(), new HashMap<>())
          .executeMigration(createInitialChangeLogs(ExecutorChangeLog.class));
    } catch (Exception ex) {
    }

    //then
    verify(lockManager, new Times(1)).close();
  }


  @Test(expected = MongockException.class)
  @SuppressWarnings("unchecked")
  public void shouldPropagateChangockException_EvenWhenThrowExIfCannotLock_IfDriverNotValidated() {
    // given
    injectDummyDependency(DummyDependencyClass.class, "Wrong parameter");
    when(changeEntryService.isAlreadyExecuted("newChangeSet", "executor")).thenReturn(false);
    doThrow(MongockException.class).when(driver).runValidation();

    // when
    new MigrationExecutor(driver, new DependencyManager(), getMigrationConfig(), new HashMap<>())
        .executeMigration(createInitialChangeLogs(ExecutorChangeLog.class));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void shouldContinueMigration_whenAChangeSetFails_ifItIsNonFailFast() throws InterruptedException {
    // given
    injectDummyDependency(DummyDependencyClass.class, new DummyDependencyClass());
    when(changeEntryService.isAlreadyExecuted("newChangeSet1", "executor")).thenReturn(false);
    when(changeEntryService.isAlreadyExecuted("changeSetNonFailFast", "executor")).thenReturn(false);
    when(changeEntryService.isAlreadyExecuted("newChangeSet2", "executor")).thenReturn(false);

    // when
    new MigrationExecutor(driver, new DependencyManager(), getMigrationConfig(), new HashMap<>())
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
    assertEquals("newChangeSet1", entry.getChangeSetMethod());
    assertEquals(ChangeState.EXECUTED, entry.getState());


    entry = entries.get(1);
    assertEquals("changeSetNonFailFast", entry.getChangeId());
    assertEquals("executor", entry.getAuthor());
    assertEquals(ExecutorWithNonFailFastChangeLog.class.getName(), entry.getChangeLogClass());
    assertEquals("changeSetNonFailFast", entry.getChangeSetMethod());
    assertEquals(ChangeState.FAILED, entry.getState());

    entry = entries.get(2);
    assertEquals("newChangeSet2", entry.getChangeId());
    assertEquals("executor", entry.getAuthor());
    assertEquals(ExecutorWithNonFailFastChangeLog.class.getName(), entry.getChangeLogClass());
    assertEquals("newChangeSet2", entry.getChangeSetMethod());
    assertEquals(ChangeState.EXECUTED, entry.getState());
  }


  @Test
  @SuppressWarnings("unchecked")
  public void shouldFail_whenRunningChangeSet_ifForbiddenParameterFromDriver() {

    when(changeEntryService.isAlreadyExecuted("withForbiddenParameter", "executor")).thenReturn(true);

    // then
    exceptionExpected.expect(MongockException.class);
    exceptionExpected.expectMessage("Error in method[ChangeLogWithForbiddenParameter.withForbiddenParameter] : Forbidden parameter[ForbiddenParameter]. Must be replaced with [String]");

    // when
    new MigrationExecutor(driver, new DependencyManager(), getMigrationConfig(), new HashMap<>())
        .executeMigration(createInitialChangeLogs(ChangeLogWithForbiddenParameter.class));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void shouldThrowException_IfChangeSetParameterfNotInterface() {
    // given
    when(changeEntryService.isAlreadyExecuted("newChangeSet", "executor")).thenReturn(false);
    when(changeEntryService.isAlreadyExecuted("withNonLockGuardedParameter", "executor")).thenReturn(true);

    // then
    exceptionExpected.expect(MongockException.class);
    exceptionExpected.expectMessage("Error in method[ExecutorChangeLog.newChangeSet] : Parameter of type [DummyDependencyClass] must be an interface");

    // when
    DependencyManager dependencyManager = new DependencyManager()
        .setLockGuardProxyFactory(new LockGuardProxyFactory(Mockito.mock(LockManager.class)))
        .addStandardDependency(new ChangeSetDependency(new DummyDependencyClass()));
    new MigrationExecutor(driver, dependencyManager, getMigrationConfig(), new HashMap<>())
        .executeMigration(createInitialChangeLogs(ExecutorChangeLog.class));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void shouldReturnProxy_IfStandardDependency() {
    // given
    when(changeEntryService.isAlreadyExecuted("withInterfaceParameter", "executor")).thenReturn(false);
    when(changeEntryService.isAlreadyExecuted("withInterfaceParameter2", "executor")).thenReturn(true);
    when(changeEntryService.isAlreadyExecuted("withNonLockGuardedParameter", "executor")).thenReturn(true);

    // when
    when(driver.getLockManager()).thenReturn(lockManager);
    DependencyManager dependencyManager = new DependencyManager()
        .addStandardDependency(new ChangeSetDependency(new InterfaceDependencyImpl()));

    new MigrationExecutor(driver, dependencyManager, getMigrationConfig(), new HashMap<>())
        .executeMigration(createInitialChangeLogs(ChangeLogWithInterfaceParameter.class));

    // then
    verify(lockManager, new Times(1)).ensureLockDefault();
  }

  @Test
  @SuppressWarnings("unchecked")
  public void proxyReturnedShouldReturnAProxy_whenCallingAMethod_IfInterface() {
    // given
    when(changeEntryService.isAlreadyExecuted("withInterfaceParameter", "executor")).thenReturn(true);
    when(changeEntryService.isAlreadyExecuted("withInterfaceParameter2", "executor")).thenReturn(false);
    when(changeEntryService.isAlreadyExecuted("withNonLockGuardedParameter", "executor")).thenReturn(true);

    // when
    when(driver.getLockManager()).thenReturn(lockManager);
    DependencyManager dependencyManager = new DependencyManager()
        .addStandardDependency(new ChangeSetDependency(new InterfaceDependencyImpl()));

    new MigrationExecutor(driver, dependencyManager, getMigrationConfig(), new HashMap<>())
        .executeMigration(createInitialChangeLogs(ChangeLogWithInterfaceParameter.class));

    // then
    verify(lockManager, new Times(2)).ensureLockDefault();
  }

  @Test
  @SuppressWarnings("unchecked")
  public void shouldNotReturnProxy_IfClassAnnotatedWithNonLockGuarded() {
    // given
    when(changeEntryService.isAlreadyExecuted("withInterfaceParameter", "executor")).thenReturn(false);
    when(changeEntryService.isAlreadyExecuted("withInterfaceParameter2", "executor")).thenReturn(true);
    when(changeEntryService.isAlreadyExecuted("withNonLockGuardedParameter", "executor")).thenReturn(true);

    // when
    when(driver.getLockManager()).thenReturn(lockManager);
    DependencyManager dependencyManager = new DependencyManager()
        .addStandardDependency(new ChangeSetDependency(new InterfaceDependencyImplNoLockGarded()));

    new MigrationExecutor(driver, dependencyManager, getMigrationConfig(), new HashMap<>())
        .executeMigration(createInitialChangeLogs(ChangeLogWithInterfaceParameter.class));

    // then
    verify(lockManager, new Times(0)).ensureLockDefault();
  }

  @Test
  @SuppressWarnings("unchecked")
  public void shouldNotReturnProxy_IfParameterAnnotatedWithNonLockGuarded() {
    // given
    when(changeEntryService.isAlreadyExecuted("withInterfaceParameter", "executor")).thenReturn(true);
    when(changeEntryService.isAlreadyExecuted("withInterfaceParameter2", "executor")).thenReturn(true);
    when(changeEntryService.isAlreadyExecuted("withNonLockGuardedParameter", "executor")).thenReturn(false);

    // when
    when(driver.getLockManager()).thenReturn(lockManager);
    DependencyManager dependencyManager = new DependencyManager()
        .addStandardDependency(new ChangeSetDependency(new InterfaceDependencyImpl()));

    new MigrationExecutor(driver, dependencyManager, getMigrationConfig(), new HashMap<>())
        .executeMigration(createInitialChangeLogs(ChangeLogWithInterfaceParameter.class));

    // then
    verify(lockManager, new Times(0)).ensureLockDefault();
  }

  @Test
  @SuppressWarnings("unchecked")
  public void shouldInjectLegacyMigrationList_whenNamed() throws InterruptedException {
    // given
    when(changeEntryService.isAlreadyExecuted("legacy_migration", "executor")).thenReturn(false);
    // when
    when(driver.getLockManager()).thenReturn(lockManager);
    LegacyMigrationMappingFields mappingFields = new LegacyMigrationMappingFields();
    mappingFields.setAuthor("AUTHOR");
    LegacyMigration dependency = new LegacyMigration() {};
    dependency.setMappingFields(mappingFields);
    DependencyManager dependencyManager = new DependencyManager()
        .addStandardDependency(new ChangeSetDependency(List.class, Collections.singletonList(new LegacyMigration() {})))
        .addStandardDependency(new ChangeSetDependency("legacyMigration2", List.class, Collections.singletonList(new LegacyMigration() {})))
        .addStandardDependency(new ChangeSetDependency("legacyMigration", List.class, Collections.singletonList(dependency)))
        .addStandardDependency(new ChangeSetDependency(List.class, Collections.singletonList(new LegacyMigration() {})))
        .addStandardDependency(new ChangeSetDependency("legacyMigration3", List.class, Collections.singletonList(new LegacyMigration() {})));

    new MigrationExecutor(driver, dependencyManager, getMigrationConfig(), new HashMap<>())
        .executeMigration(createInitialChangeLogs(LegacyMigrationChangeLog.class));

    // then
    LegacyMigrationChangeLog.latch.await(5, TimeUnit.SECONDS);
  }

  @Test
  public void shouldSkipMigration_whenChangeLogWithNoChangeSet() {
    when(driver.getLockManager()).thenReturn(lockManager);
    // when
    new MigrationExecutor(driver, new DependencyManager(), getMigrationConfig(), new HashMap<>())
        .executeMigration(createInitialChangeLogs(ChangeLogWithNoChangeSet.class));

    //then
    ArgumentCaptor<String> changeSetIdCaptor = ArgumentCaptor.forClass(String.class);
    // Lock should not be acquired because there is no change set.
    verify(lockManager, new Times(0)).acquireLockDefault();
  }

  @Test
  public void shouldSkipMigration_whenAllChangeSetItemsAlreadyExecuted() {
    // given
    when(changeEntryService.isAlreadyExecuted("alreadyExecuted", "executor")).thenReturn(true);
    when(changeEntryService.isAlreadyExecuted("alreadyExecuted2", "executor")).thenReturn(true);

    when(driver.getLockManager()).thenReturn(lockManager);

    // when
    new MigrationExecutor(driver, new DependencyManager(), getMigrationConfig(), new HashMap<>())
        .executeMigration(createInitialChangeLogs(ChangeLogAlreadyExecuted.class));

    //then
    // Lock should not be acquired because all items are already executed.
    verify(lockManager, new Times(0)).acquireLockDefault();
  }

  @Test
  @SuppressWarnings("unchecked")
  public void shouldNotSkipMigration_whenAllChangeSetItemsAlreadyExecuted_ifAtLeastOneChangeSetFlaggedAsRunAlways() {
    // given
    injectDummyDependency(DummyDependencyClass.class, new DummyDependencyClass());
    when(changeEntryService.isAlreadyExecuted("alreadyExecuted", "executor")).thenReturn(true);
    when(changeEntryService.isAlreadyExecuted("alreadyExecuted2", "executor")).thenReturn(true);
    when(changeEntryService.isAlreadyExecuted("alreadyExecutedRunAlways", "executor")).thenReturn(true);

    when(driver.getLockManager()).thenReturn(lockManager);

    // when
    new MigrationExecutor(driver, new DependencyManager(), getMigrationConfig(), new HashMap<>())
        .executeMigration(createInitialChangeLogs(ChangeLogAlreadyExecutedRunAlways.class));

    //then
    verify(lockManager, new Times(1)).acquireLockDefault();

    ArgumentCaptor<ChangeEntry> changeEntryCaptor = ArgumentCaptor.forClass(ChangeEntry.class);
    // ChangeEntry for ChangeSet "alreadyExecutedRunAlways" should be stored
    verify(changeEntryService, new Times(1)).save(changeEntryCaptor.capture());
  }

  private SortedSet<ChangeLogItem> createInitialChangeLogs(Class<?> executorChangeLogClass) {
    return new ChangeLogService(Collections.singletonList(executorChangeLogClass.getPackage().getName()), Collections.emptyList(),  "0", String.valueOf(Integer.MAX_VALUE))
        .fetchChangeLogs();
  }

  private void injectDummyDependency(Class<?> type, Object instance) {
    Set<ChangeSetDependency> dependencies = new HashSet<>();
    dependencies.add(new ChangeSetDependency(type, instance));
    when(driver.getDependencies()).thenReturn(dependencies);
  }


  private MigrationExecutorConfiguration getMigrationConfig() {
    return getMigrationConfig(false);
  }

  private MigrationExecutorConfiguration getMigrationConfig(boolean trackIgnored) {
    return new MigrationExecutorConfiguration(trackIgnored);
  }

}
