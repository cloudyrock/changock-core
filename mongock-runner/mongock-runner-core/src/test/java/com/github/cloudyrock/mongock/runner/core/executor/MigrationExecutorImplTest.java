package com.github.cloudyrock.mongock.runner.core.executor;


import com.github.cloudyrock.mongock.ChangeLogItem;
import com.github.cloudyrock.mongock.ChangeSetItem;
import com.github.cloudyrock.mongock.config.LegacyMigration;
import com.github.cloudyrock.mongock.config.LegacyMigrationMappingFields;
import com.github.cloudyrock.mongock.config.MongockConfiguration;
import com.github.cloudyrock.mongock.driver.api.driver.ChangeSetDependency;
import com.github.cloudyrock.mongock.driver.api.driver.ConnectionDriver;
import com.github.cloudyrock.mongock.driver.api.entry.ChangeEntry;
import com.github.cloudyrock.mongock.driver.api.entry.ChangeEntryService;
import com.github.cloudyrock.mongock.driver.api.entry.ChangeState;
import com.github.cloudyrock.mongock.driver.api.lock.LockManager;
import com.github.cloudyrock.mongock.driver.api.lock.guard.proxy.LockGuardProxyFactory;
import com.github.cloudyrock.mongock.exception.MongockException;
import com.github.cloudyrock.mongock.runner.core.changelogs.executor.test1.ExecutorChangeLog;
import com.github.cloudyrock.mongock.runner.core.changelogs.executor.test3_with_nonFailFast.ExecutorWithNonFailFastChangeLog;
import com.github.cloudyrock.mongock.runner.core.changelogs.executor.test4_with_failfast.ExecutorWithFailFastChangeLog;
import com.github.cloudyrock.mongock.runner.core.changelogs.executor.test5_with_changelognonfailfast.ExecutorWithChangeLogNonFailFastChangeLog1;
import com.github.cloudyrock.mongock.runner.core.changelogs.executor.test5_with_changelognonfailfast.ExecutorWithChangeLogNonFailFastChangeLog2;
import com.github.cloudyrock.mongock.runner.core.changelogs.executor.test6_with_changelogfailfast.ExecutorWithChangeLogFailFastChangeLog1;
import com.github.cloudyrock.mongock.runner.core.changelogs.executor.withInterfaceParameter.ChangeLogWithInterfaceParameter;
import com.github.cloudyrock.mongock.runner.core.changelogs.legacymigration.LegacyMigrationChangeLog;
import com.github.cloudyrock.mongock.runner.core.changelogs.skipmigration.alreadyexecuted.ChangeLogAlreadyExecuted;
import com.github.cloudyrock.mongock.runner.core.changelogs.skipmigration.runalways.ChangeLogAlreadyExecutedRunAlways;
import com.github.cloudyrock.mongock.runner.core.changelogs.skipmigration.withnochangeset.ChangeLogWithNoChangeSet;
import com.github.cloudyrock.mongock.runner.core.changelogs.withRollback.ChangeLogWithRollback;
import com.github.cloudyrock.mongock.runner.core.executor.changelog.ChangeLogService;
import com.github.cloudyrock.mongock.runner.core.executor.dependency.DependencyManager;
import com.github.cloudyrock.mongock.runner.core.executor.operation.change.MigrationExecutor;
import com.github.cloudyrock.mongock.runner.core.util.DummyDependencyClass;
import com.github.cloudyrock.mongock.runner.core.util.InterfaceDependencyImpl;
import com.github.cloudyrock.mongock.runner.core.util.InterfaceDependencyImplNoLockGarded;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.internal.verification.Times;

import javax.inject.Named;
import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MigrationExecutorImplTest {
  private static final Function<Parameter, String> DEFAULT_PARAM_NAME_PROVIDER = parameter -> parameter.isAnnotationPresent(Named.class) ? parameter.getAnnotation(Named.class).value() : null;
  @Rule
  public ExpectedException exceptionExpected = ExpectedException.none();
  private ChangeEntryService<ChangeEntry> changeEntryService;
  private LockManager lockManager;
  private ConnectionDriver<ChangeEntry> driver;
  private TransactionableConnectionDriver transactionableDriver;

  @Before
  public void setUp() {
    lockManager = mock(LockManager.class);
    changeEntryService = mock(ChangeEntryService.class);

    driver = mock(ConnectionDriver.class);
    when(driver.getLockManager()).thenReturn(lockManager);
    when(driver.getChangeEntryService()).thenReturn(changeEntryService);

    transactionableDriver = mock(TransactionableConnectionDriver.class);
    when(transactionableDriver.getLockManager()).thenReturn(lockManager);
    when(transactionableDriver.getChangeEntryService()).thenReturn(changeEntryService);
    when(transactionableDriver.getTransactioner()).thenReturn(Optional.of(Runnable::run));
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
    when(changeEntryService.isAlreadyExecuted("runAlwaysAndAlreadyExecutedChangeSet", "executor")).thenReturn(false);
    when(changeEntryService.isAlreadyExecuted("alreadyExecuted", "executor")).thenReturn(true);

    // when
    MongockConfiguration config = new MongockConfiguration();
    config.setServiceIdentifier("myService");
    config.setTrackIgnored(trackingIgnored);
    new MigrationExecutor("", createInitialChangeLogsByPackage(ExecutorChangeLog.class), driver, new DependencyManager(), DEFAULT_PARAM_NAME_PROVIDER, config)
        .executeMigration();

    assertTrue("Changelog's methods have not been fully executed", ExecutorChangeLog.latch.await(1, TimeUnit.NANOSECONDS));
    // then
    ArgumentCaptor<ChangeEntry> captor = ArgumentCaptor.forClass(ChangeEntry.class);
    verify(changeEntryService, new Times(trackingIgnored ? 4 : 3)).saveOrUpdate(captor.capture());

    List<ChangeEntry> entries = captor.getAllValues();
    assertEquals(trackingIgnored ? 4 : 3, entries.size());
    ChangeEntry entry = entries.get(0);
    assertEquals("newChangeSet", entry.getChangeId());
    assertEquals("executor", entry.getAuthor());
    assertEquals(ExecutorChangeLog.class.getName(), entry.getChangeLogClass());
    assertEquals("newChangeSet", entry.getChangeSetMethod());
    assertEquals(ChangeState.EXECUTED, entry.getState());
    assertTrue(entry.getExecutionHostname().endsWith("-myService"));

    entry = entries.get(1);
    assertEquals("runAlwaysAndNewChangeSet", entry.getChangeId());
    assertEquals("executor", entry.getAuthor());
    assertEquals(ExecutorChangeLog.class.getName(), entry.getChangeLogClass());
    assertEquals("runAlwaysAndNewChangeSet", entry.getChangeSetMethod());
    assertEquals(ChangeState.EXECUTED, entry.getState());
    assertTrue(entry.getExecutionHostname().endsWith("-myService"));

    int nextIndex = 2;
    if (trackingIgnored) {
      entry = entries.get(nextIndex);
      assertEquals("alreadyExecuted", entry.getChangeId());
      assertEquals("executor", entry.getAuthor());
      assertEquals(ExecutorChangeLog.class.getName(), entry.getChangeLogClass());
      assertEquals("alreadyExecuted", entry.getChangeSetMethod());
      assertEquals(ChangeState.IGNORED, entry.getState());
      assertTrue(entry.getExecutionHostname().endsWith("-myService"));
      nextIndex++;
    }

    entry = entries.get(nextIndex);
    assertEquals("runAlwaysAndAlreadyExecutedChangeSet", entry.getChangeId());
    assertEquals("executor", entry.getAuthor());
    assertEquals(ExecutorChangeLog.class.getName(), entry.getChangeLogClass());
    assertEquals("runAlwaysAndAlreadyExecutedChangeSet", entry.getChangeSetMethod());
    assertEquals(ChangeState.EXECUTED, entry.getState());
    assertTrue(entry.getExecutionHostname().endsWith("-myService"));
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
      MongockConfiguration config = new MongockConfiguration();
      config.setServiceIdentifier("myService");
      config.setTrackIgnored(false);
      MigrationExecutor executor = new MigrationExecutor("", createInitialChangeLogsByPackage(ExecutorWithFailFastChangeLog.class), driver, new DependencyManager(), DEFAULT_PARAM_NAME_PROVIDER, config);
      executor
          .executeMigration();
    } catch (Exception ex) {
      //ignored
    }

    assertTrue("Changelog's methods have not been fully executed", ExecutorWithFailFastChangeLog.latch.await(1, TimeUnit.NANOSECONDS));
    // then
    ArgumentCaptor<ChangeEntry> captor = ArgumentCaptor.forClass(ChangeEntry.class);
    verify(changeEntryService, new Times(3)).saveOrUpdate(captor.capture());

    List<ChangeEntry> entries = captor.getAllValues();
    assertEquals(3, entries.size());
    ChangeEntry entry = entries.get(0);
    assertEquals("newChangeSet", entry.getChangeId());
    assertEquals("executor", entry.getAuthor());
    assertEquals(ExecutorWithFailFastChangeLog.class.getName(), entry.getChangeLogClass());
    assertEquals("newChangeSet", entry.getChangeSetMethod());
    assertEquals(ChangeState.EXECUTED, entry.getState());
    assertTrue(entry.getExecutionHostname().endsWith("-myService"));

    entry = entries.get(1);
    assertEquals("runAlwaysAndNewChangeSet", entry.getChangeId());
    assertEquals("executor", entry.getAuthor());
    assertEquals(ExecutorWithFailFastChangeLog.class.getName(), entry.getChangeLogClass());
    assertEquals("runAlwaysAndNewChangeSet", entry.getChangeSetMethod());
    assertEquals(ChangeState.EXECUTED, entry.getState());
    assertTrue(entry.getExecutionHostname().endsWith("-myService"));

    entry = entries.get(2);
    assertEquals("throwsException", entry.getChangeId());
    assertEquals("executor", entry.getAuthor());
    assertEquals(ExecutorWithFailFastChangeLog.class.getName(), entry.getChangeLogClass());
    assertEquals("throwsException", entry.getChangeSetMethod());
    assertEquals(ChangeState.FAILED, entry.getState());
    assertTrue(entry.getExecutionHostname().endsWith("-myService"));
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
    MongockConfiguration config = new MongockConfiguration();
    config.setServiceIdentifier("myService");
    config.setTrackIgnored(false);
    new MigrationExecutor("", createInitialChangeLogsByPackage(ExecutorChangeLog.class), driver, new DependencyManager(), DEFAULT_PARAM_NAME_PROVIDER, config)
        .executeMigration();
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
    MongockConfiguration config = new MongockConfiguration();
    config.setServiceIdentifier("myService");
    config.setTrackIgnored(false);
    new MigrationExecutor("", createInitialChangeLogsByPackage(ExecutorChangeLog.class), driver, new DependencyManager(), DEFAULT_PARAM_NAME_PROVIDER, config)
        .executeMigration();
  }

  @Test
  @SuppressWarnings("unchecked")
  public void shouldCloseLockManager_WhenException() {
    // given
    injectDummyDependency(DummyDependencyClass.class, "Wrong parameter");
    when(changeEntryService.isAlreadyExecuted("newChangeSet", "executor")).thenReturn(false);

    // when
    try {
      MongockConfiguration config = new MongockConfiguration();
      config.setServiceIdentifier("myService");
      config.setTrackIgnored(false);
      new MigrationExecutor("", createInitialChangeLogsByPackage(ExecutorChangeLog.class), driver, new DependencyManager(), DEFAULT_PARAM_NAME_PROVIDER, config)
          .executeMigration();
    } catch (Exception ex) {
    }

    //then
    verify(lockManager, new Times(1)).close();
  }


  @Test(expected = MongockException.class)
  @SuppressWarnings("unchecked")
  public void shouldPropagateMongockException_EvenWhenThrowExIfCannotLock_IfDriverNotValidated() {
    // given
    injectDummyDependency(DummyDependencyClass.class, "Wrong parameter");
    when(changeEntryService.isAlreadyExecuted("newChangeSet", "executor")).thenReturn(false);
    doThrow(MongockException.class).when(driver).runValidation();

    // when
    MongockConfiguration config = new MongockConfiguration();
    config.setServiceIdentifier("myService");
    config.setTrackIgnored(false);
    new MigrationExecutor("", createInitialChangeLogsByPackage(ExecutorChangeLog.class), driver, new DependencyManager(), DEFAULT_PARAM_NAME_PROVIDER, config)
        .executeMigration();
  }

  @Test
  @SuppressWarnings("unchecked")
  public void shouldContinueMigration_whenAChangeSetFails_ifChangeSetIsNonFailFast() throws InterruptedException {
    // given
    injectDummyDependency(DummyDependencyClass.class, new DummyDependencyClass());
    when(changeEntryService.isAlreadyExecuted("newChangeSet1", "executor")).thenReturn(false);
    when(changeEntryService.isAlreadyExecuted("changeSetNonFailFast", "executor")).thenReturn(false);
    when(changeEntryService.isAlreadyExecuted("newChangeSet2", "executor")).thenReturn(false);

    // when
    MongockConfiguration config = new MongockConfiguration();
    config.setServiceIdentifier("myService");
    config.setTrackIgnored(false);
    new MigrationExecutor("", createInitialChangeLogsByPackage(ExecutorWithNonFailFastChangeLog.class), driver, new DependencyManager(), DEFAULT_PARAM_NAME_PROVIDER, config)
        .executeMigration();

    assertTrue("Changelog's methods have not been fully executed", ExecutorWithNonFailFastChangeLog.latch.await(1, TimeUnit.NANOSECONDS));
    // then
    ArgumentCaptor<ChangeEntry> captor = ArgumentCaptor.forClass(ChangeEntry.class);
    verify(changeEntryService, new Times(3)).saveOrUpdate(captor.capture());

    List<ChangeEntry> entries = captor.getAllValues();
    assertEquals(3, entries.size());

    ChangeEntry entry = entries.get(0);
    assertEquals("newChangeSet1", entry.getChangeId());
    assertEquals("executor", entry.getAuthor());
    assertEquals(ExecutorWithNonFailFastChangeLog.class.getName(), entry.getChangeLogClass());
    assertEquals("newChangeSet1", entry.getChangeSetMethod());
    assertEquals(ChangeState.EXECUTED, entry.getState());
    assertTrue(entry.getExecutionHostname().endsWith("-myService"));


    entry = entries.get(1);
    assertEquals("changeSetNonFailFast", entry.getChangeId());
    assertEquals("executor", entry.getAuthor());
    assertEquals(ExecutorWithNonFailFastChangeLog.class.getName(), entry.getChangeLogClass());
    assertEquals("changeSetNonFailFast", entry.getChangeSetMethod());
    assertEquals(ChangeState.FAILED, entry.getState());
    assertTrue(entry.getExecutionHostname().endsWith("-myService"));

    entry = entries.get(2);
    assertEquals("newChangeSet2", entry.getChangeId());
    assertEquals("executor", entry.getAuthor());
    assertEquals(ExecutorWithNonFailFastChangeLog.class.getName(), entry.getChangeLogClass());
    assertEquals("newChangeSet2", entry.getChangeSetMethod());
    assertEquals(ChangeState.EXECUTED, entry.getState());
    assertTrue(entry.getExecutionHostname().endsWith("-myService"));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void shouldContinueMigration_whenAChangeSetFails_ifChangeLogIsNonFailFast() throws InterruptedException {
    // given
    injectDummyDependency(DummyDependencyClass.class, new DummyDependencyClass());
    when(changeEntryService.isAlreadyExecuted("newChangeSet11", "executor")).thenReturn(false);
    when(changeEntryService.isAlreadyExecuted("newChangeSet12", "executor")).thenReturn(false);
    when(changeEntryService.isAlreadyExecuted("newChangeSet13", "executor")).thenReturn(false);
    when(changeEntryService.isAlreadyExecuted("newChangeSet21", "executor")).thenReturn(false);
    when(changeEntryService.isAlreadyExecuted("newChangeSet22", "executor")).thenReturn(false);

    // when
    try {
      MongockConfiguration config = new MongockConfiguration();
      config.setServiceIdentifier("myService");
      config.setTrackIgnored(false);
      new MigrationExecutor("", createInitialChangeLogsByPackage(ExecutorWithChangeLogNonFailFastChangeLog1.class), driver, new DependencyManager(), DEFAULT_PARAM_NAME_PROVIDER, config)
          .executeMigration();
    } catch (Exception ex) {
    }

    assertTrue("Changelog's (1) methods have not been fully executed", ExecutorWithChangeLogNonFailFastChangeLog1.latch.await(1, TimeUnit.NANOSECONDS));
    assertTrue("Changelog's (2) methods have not been fully executed", ExecutorWithChangeLogNonFailFastChangeLog2.latch.await(1, TimeUnit.NANOSECONDS));

    // then
    ArgumentCaptor<ChangeEntry> captor = ArgumentCaptor.forClass(ChangeEntry.class);
    verify(changeEntryService, new Times(4)).saveOrUpdate(captor.capture());

    List<ChangeEntry> entries = captor.getAllValues();
    assertEquals(4, entries.size());

    ChangeEntry entry = entries.get(0);
    assertEquals("newChangeSet11", entry.getChangeId());
    assertEquals("executor", entry.getAuthor());
    assertEquals(ExecutorWithChangeLogNonFailFastChangeLog1.class.getName(), entry.getChangeLogClass());
    assertEquals("newChangeSet11", entry.getChangeSetMethod());
    assertEquals(ChangeState.EXECUTED, entry.getState());
    assertTrue(entry.getExecutionHostname().endsWith("-myService"));

    entry = entries.get(1);
    assertEquals("newChangeSet12", entry.getChangeId());
    assertEquals("executor", entry.getAuthor());
    assertEquals(ExecutorWithChangeLogNonFailFastChangeLog1.class.getName(), entry.getChangeLogClass());
    assertEquals("newChangeSet12", entry.getChangeSetMethod());
    assertEquals(ChangeState.FAILED, entry.getState());
    assertTrue(entry.getExecutionHostname().endsWith("-myService"));

    entry = entries.get(2);
    assertEquals("newChangeSet21", entry.getChangeId());
    assertEquals("executor", entry.getAuthor());
    assertEquals(ExecutorWithChangeLogNonFailFastChangeLog2.class.getName(), entry.getChangeLogClass());
    assertEquals("newChangeSet21", entry.getChangeSetMethod());
    assertEquals(ChangeState.EXECUTED, entry.getState());
    assertTrue(entry.getExecutionHostname().endsWith("-myService"));

    entry = entries.get(3);
    assertEquals("newChangeSet22", entry.getChangeId());
    assertEquals("executor", entry.getAuthor());
    assertEquals(ExecutorWithChangeLogNonFailFastChangeLog2.class.getName(), entry.getChangeLogClass());
    assertEquals("newChangeSet22", entry.getChangeSetMethod());
    assertEquals(ChangeState.EXECUTED, entry.getState());
    assertTrue(entry.getExecutionHostname().endsWith("-myService"));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void shouldAbortMigration_whenAChangeSetFails_ifChangeLogIsFailFast() throws InterruptedException {
    // given
    injectDummyDependency(DummyDependencyClass.class, new DummyDependencyClass());
    when(changeEntryService.isAlreadyExecuted("newChangeSet11", "executor")).thenReturn(false);
    when(changeEntryService.isAlreadyExecuted("newChangeSet12", "executor")).thenReturn(false);
    when(changeEntryService.isAlreadyExecuted("newChangeSet13", "executor")).thenReturn(false);
    when(changeEntryService.isAlreadyExecuted("newChangeSet21", "executor")).thenReturn(false);
    when(changeEntryService.isAlreadyExecuted("newChangeSet22", "executor")).thenReturn(false);

    // when
    try {
      MongockConfiguration config = new MongockConfiguration();
      config.setServiceIdentifier("myService");
      config.setTrackIgnored(false);
      new MigrationExecutor("", createInitialChangeLogsByPackage(ExecutorWithChangeLogFailFastChangeLog1.class), driver, new DependencyManager(), DEFAULT_PARAM_NAME_PROVIDER, config)
          .executeMigration();
    } catch (Exception ex) {
    }

    assertTrue("Changelog's methods have not been fully executed", ExecutorWithChangeLogFailFastChangeLog1.latch.await(1, TimeUnit.NANOSECONDS));

    // then
    ArgumentCaptor<ChangeEntry> captor = ArgumentCaptor.forClass(ChangeEntry.class);
    verify(changeEntryService, new Times(2)).saveOrUpdate(captor.capture());

    List<ChangeEntry> entries = captor.getAllValues();
    assertEquals(2, entries.size());

    ChangeEntry entry = entries.get(0);
    assertEquals("newChangeSet11", entry.getChangeId());
    assertEquals("executor", entry.getAuthor());
    assertEquals(ExecutorWithChangeLogFailFastChangeLog1.class.getName(), entry.getChangeLogClass());
    assertEquals("newChangeSet11", entry.getChangeSetMethod());
    assertEquals(ChangeState.EXECUTED, entry.getState());
    assertTrue(entry.getExecutionHostname().endsWith("-myService"));

    entry = entries.get(1);
    assertEquals("newChangeSet12", entry.getChangeId());
    assertEquals("executor", entry.getAuthor());
    assertEquals(ExecutorWithChangeLogFailFastChangeLog1.class.getName(), entry.getChangeLogClass());
    assertEquals("newChangeSet12", entry.getChangeSetMethod());
    assertEquals(ChangeState.FAILED, entry.getState());
    assertTrue(entry.getExecutionHostname().endsWith("-myService"));
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

    MongockConfiguration config = new MongockConfiguration();
    config.setServiceIdentifier("myService");
    config.setTrackIgnored(false);
    new MigrationExecutor("", createInitialChangeLogsByPackage(ChangeLogWithInterfaceParameter.class), driver, dependencyManager, DEFAULT_PARAM_NAME_PROVIDER, config)
        .executeMigration();

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

    MongockConfiguration config = new MongockConfiguration();
    config.setServiceIdentifier("myService");
    config.setTrackIgnored(false);
    new MigrationExecutor("", createInitialChangeLogsByPackage(ChangeLogWithInterfaceParameter.class), driver, dependencyManager, DEFAULT_PARAM_NAME_PROVIDER, config)
        .executeMigration();

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

    MongockConfiguration config = new MongockConfiguration();
    config.setServiceIdentifier("myService");
    config.setTrackIgnored(false);
    new MigrationExecutor("", createInitialChangeLogsByPackage(ChangeLogWithInterfaceParameter.class), driver, dependencyManager, DEFAULT_PARAM_NAME_PROVIDER, config)
        .executeMigration();

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

    MongockConfiguration config = new MongockConfiguration();
    config.setServiceIdentifier("myService");
    config.setTrackIgnored(false);
    new MigrationExecutor("", createInitialChangeLogsByPackage(ChangeLogWithInterfaceParameter.class), driver, dependencyManager, DEFAULT_PARAM_NAME_PROVIDER, config)
        .executeMigration();

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
    LegacyMigration dependency = new LegacyMigration() {
    };
    dependency.setMappingFields(mappingFields);
    DependencyManager dependencyManager = new DependencyManager()
        .addStandardDependency(new ChangeSetDependency(List.class, Collections.singletonList(new LegacyMigration() {
        })))
        .addStandardDependency(new ChangeSetDependency("legacyMigration2", List.class, Collections.singletonList(new LegacyMigration() {
        })))
        .addStandardDependency(new ChangeSetDependency("legacyMigration", List.class, Collections.singletonList(dependency)))
        .addStandardDependency(new ChangeSetDependency(List.class, Collections.singletonList(new LegacyMigration() {
        })))
        .addStandardDependency(new ChangeSetDependency("legacyMigration3", List.class, Collections.singletonList(new LegacyMigration() {
        })));

    MongockConfiguration config = new MongockConfiguration();
    config.setServiceIdentifier("myService");
    config.setTrackIgnored(false);
    new MigrationExecutor("", createInitialChangeLogsByPackage(LegacyMigrationChangeLog.class), driver, dependencyManager, DEFAULT_PARAM_NAME_PROVIDER, config)
        .executeMigration();

    // then
    LegacyMigrationChangeLog.latch.await(5, TimeUnit.SECONDS);
  }

  @Test
  public void shouldSkipMigration_whenChangeLogWithNoChangeSet() {
    when(driver.getLockManager()).thenReturn(lockManager);
    // when
    MongockConfiguration config = new MongockConfiguration();
    config.setServiceIdentifier("myService");
    config.setTrackIgnored(false);
    new MigrationExecutor("", createInitialChangeLogsByPackage(ChangeLogWithNoChangeSet.class), driver, new DependencyManager(), DEFAULT_PARAM_NAME_PROVIDER, config)
        .executeMigration();

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
    MongockConfiguration config = new MongockConfiguration();
    config.setServiceIdentifier("myService");
    config.setTrackIgnored(false);
    new MigrationExecutor("", createInitialChangeLogsByPackage(ChangeLogAlreadyExecuted.class), driver, new DependencyManager(), DEFAULT_PARAM_NAME_PROVIDER, config)
        .executeMigration();

    //then
    // Lock should not be acquired because all items are already executed.
    verify(lockManager, new Times(0)).acquireLockDefault();
  }

  @Test
  @SuppressWarnings("unchecked")
  public void shouldStoreChangeLog_whenRunAlways_ifNotAlreadyExecuted() {
    // given
    injectDummyDependency(DummyDependencyClass.class, new DummyDependencyClass());
    when(changeEntryService.isAlreadyExecuted("alreadyExecuted", "executor")).thenReturn(true);
    when(changeEntryService.isAlreadyExecuted("alreadyExecuted2", "executor")).thenReturn(true);
    when(changeEntryService.isAlreadyExecuted("alreadyExecutedRunAlways", "executor")).thenReturn(false);

    when(driver.getLockManager()).thenReturn(lockManager);

    // when
    MongockConfiguration config = new MongockConfiguration();
    config.setServiceIdentifier("myService");
    config.setTrackIgnored(false);
    new MigrationExecutor("", createInitialChangeLogsByPackage(ChangeLogAlreadyExecutedRunAlways.class), driver, new DependencyManager(), DEFAULT_PARAM_NAME_PROVIDER, config)
        .executeMigration();

    //then
    verify(lockManager, new Times(1)).acquireLockDefault();

    ArgumentCaptor<ChangeEntry> changeEntryCaptor = ArgumentCaptor.forClass(ChangeEntry.class);
    // ChangeEntry for ChangeSet "alreadyExecutedRunAlways" should be stored
    verify(changeEntryService, new Times(1)).saveOrUpdate(changeEntryCaptor.capture());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void shouldNotStoreChangeLog_whenRunAlways_ifAlreadyExecuted() {
    // given
    injectDummyDependency(DummyDependencyClass.class, new DummyDependencyClass());
    when(changeEntryService.isAlreadyExecuted("alreadyExecuted", "executor")).thenReturn(true);
    when(changeEntryService.isAlreadyExecuted("alreadyExecuted2", "executor")).thenReturn(true);
    when(changeEntryService.isAlreadyExecuted("alreadyExecutedRunAlways", "executor")).thenReturn(true);

    when(driver.getLockManager()).thenReturn(lockManager);

    // when
    MongockConfiguration config = new MongockConfiguration();
    config.setServiceIdentifier("myService");
    config.setTrackIgnored(false);
    new MigrationExecutor("", createInitialChangeLogsByPackage(ChangeLogAlreadyExecutedRunAlways.class), driver, new DependencyManager(), DEFAULT_PARAM_NAME_PROVIDER, config)
        .executeMigration();

    //then
    verify(lockManager, new Times(1)).acquireLockDefault();

    ArgumentCaptor<ChangeEntry> changeEntryCaptor = ArgumentCaptor.forClass(ChangeEntry.class);
    // ChangeEntry for ChangeSet "alreadyExecutedRunAlways" should not be stored
    verify(changeEntryService, new Times(0)).saveOrUpdate(changeEntryCaptor.capture());
  }

  @Test
  public void shouldRollback_whenChangeSetFails_ifNoTransaction() throws InterruptedException {
    // given
    when(transactionableDriver.getLockManager()).thenReturn(lockManager);

    // when
    MongockConfiguration config = new MongockConfiguration();
    config.setServiceIdentifier("myService");
    config.setTrackIgnored(false);

    ChangeLogService changeLogService = new ChangeLogService();
    changeLogService.setChangeLogsBaseClassList(Collections.singletonList(ChangeLogWithRollback.class));

    Assert.assertThrows(
        MongockException.class,
        () -> new MigrationExecutor("", changeLogService.fetchChangeLogs(), driver, new DependencyManager(), DEFAULT_PARAM_NAME_PROVIDER, config)
            .executeMigration());

    assertTrue("Rollback method wasn't executed", ChangeLogWithRollback.rollbackCalledLatch.await(5, TimeUnit.NANOSECONDS));

    ArgumentCaptor<ChangeEntry> changeEntryCaptor = ArgumentCaptor.forClass(ChangeEntry.class);
    verify(changeEntryService, new Times(4))
        .saveOrUpdate(changeEntryCaptor.capture());

    List<ChangeEntry> allValues = changeEntryCaptor.getAllValues();
    assertEquals(ChangeState.EXECUTED, allValues.get(0).getState());
    assertEquals("changeset_with_rollback_1", allValues.get(0).getChangeId());

    assertEquals(ChangeState.FAILED, allValues.get(1).getState());
    assertEquals("changeset_with_rollback_2", allValues.get(1).getChangeId());

    assertEquals(ChangeState.ROLLED_BACK, allValues.get(2).getState());
    assertEquals("changeset_with_rollback_2", allValues.get(2).getChangeId());

    assertEquals(ChangeState.ROLLED_BACK, allValues.get(3).getState());
    assertEquals("changeset_with_rollback_1", allValues.get(3).getChangeId());


  }


  //TODO what happens if rollback fails?
//  @Test
//  public void shouldReturnFailedChangeEntry_whenRollback_ifThrowsRollbackThrowsException() throws InterruptedException {
//    // given
//    when(transactionableDriver.getLockManager()).thenReturn(lockManager);
//
//    // when
//    MongockConfiguration config = new MongockConfiguration();
//    config.setServiceIdentifier("myService");
//    config.setTrackIgnored(false);
//
//    ChangeLogService changeLogService = new ChangeLogService();
//    changeLogService.setChangeLogsBaseClassList(Collections.singletonList(ChangeLogWithRollbackThrowingException.class));
//
//    Assert.assertThrows(
//        MongockException.class,
//        () -> new MigrationExecutor("", changeLogService.fetchChangeLogs(), driver, new DependencyManager(), DEFAULT_PARAM_NAME_PROVIDER, config)
//            .executeMigration());
//
//    assertTrue("Rollback method wasn't executed", ChangeLogWithRollbackThrowingException.rollbackCalledLatch.await(1, TimeUnit.NANOSECONDS));
//
//    ArgumentCaptor<ChangeEntry> changeEntryCaptor = ArgumentCaptor.forClass(ChangeEntry.class);
//    verify(changeEntryService, new Times(1)).save(changeEntryCaptor.capture());
//
//    ChangeEntry changeEntry = changeEntryCaptor.getValue();
//    assertEquals(ChangeState.ROLLBACK_FAILED, changeEntry.getState());
//
//  }


  private SortedSet<ChangeLogItem<ChangeSetItem>> createInitialChangeLogsByPackage(Class<?>... executorChangeLogClass) {
    List<String> packages = Stream.of(executorChangeLogClass)
        .map(clazz -> clazz.getPackage().getName())
        .collect(Collectors.toList());
    ChangeLogService changeLogService = new ChangeLogService();
    changeLogService.setChangeLogsBasePackageList(packages);
    return changeLogService.fetchChangeLogs();

  }


  private void injectDummyDependency(Class<?> type, Object instance) {
    Set<ChangeSetDependency> dependencies = new HashSet<>();
    dependencies.add(new ChangeSetDependency(type, instance));
    when(driver.getDependencies()).thenReturn(dependencies);
  }

  private abstract static class TransactionableConnectionDriver implements ConnectionDriver<ChangeEntry> {
  }
}
