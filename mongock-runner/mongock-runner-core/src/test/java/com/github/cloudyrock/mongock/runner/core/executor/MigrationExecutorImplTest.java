package com.github.cloudyrock.mongock.runner.core.executor;


import com.github.cloudyrock.mongock.ChangeLogItem;
import com.github.cloudyrock.mongock.config.LegacyMigration;
import com.github.cloudyrock.mongock.config.LegacyMigrationMappingFields;
import com.github.cloudyrock.mongock.config.MongockConfiguration;
import com.github.cloudyrock.mongock.driver.api.driver.ChangeSetDependency;
import com.github.cloudyrock.mongock.driver.api.driver.ConnectionDriver;
import com.github.cloudyrock.mongock.driver.api.driver.Transactioner;
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
import com.github.cloudyrock.mongock.runner.core.changelogs.postmigration.ChangeLogPostMigration;
import com.github.cloudyrock.mongock.runner.core.changelogs.premigration.ChangeLogPreMigration;
import com.github.cloudyrock.mongock.runner.core.changelogs.prepostmigration.ChangeLogPrePostMigration;
import com.github.cloudyrock.mongock.runner.core.changelogs.skipmigration.alreadyexecuted.ChangeLogAlreadyExecuted;
import com.github.cloudyrock.mongock.runner.core.changelogs.skipmigration.runalways.ChangeLogAlreadyExecutedRunAlways;
import com.github.cloudyrock.mongock.runner.core.changelogs.skipmigration.withnochangeset.ChangeLogWithNoChangeSet;
import com.github.cloudyrock.mongock.runner.core.executor.changelog.ChangeLogService;
import com.github.cloudyrock.mongock.runner.core.executor.dependency.DependencyManager;
import com.github.cloudyrock.mongock.runner.core.executor.operation.change.MigrationExecutor;
import com.github.cloudyrock.mongock.runner.core.util.DummyDependencyClass;
import com.github.cloudyrock.mongock.runner.core.util.InterfaceDependencyImpl;
import com.github.cloudyrock.mongock.runner.core.util.InterfaceDependencyImplNoLockGarded;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MigrationExecutorImplTest {
  private static final Function<Parameter, String> DEFAULT_PARAM_NAME_PROVIDER = parameter -> parameter.isAnnotationPresent(Named.class) ? parameter.getAnnotation(Named.class).value() : null;

  private ChangeEntryService changeEntryService;
  private LockManager lockManager;
  private ConnectionDriver driver;
  private TransactionableConnectionDriver transactionableDriver;

  @Rule
  public ExpectedException exceptionExpected = ExpectedException.none();

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
    when(transactionableDriver.getTransactioner()).thenReturn(Optional.of((Transactioner) Runnable::run));
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
    new MigrationExecutor(driver, new DependencyManager(), DEFAULT_PARAM_NAME_PROVIDER, config)
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
      MigrationExecutor executor = new MigrationExecutor(driver, new DependencyManager(), DEFAULT_PARAM_NAME_PROVIDER, config);
      executor
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
    new MigrationExecutor(driver, new DependencyManager(), DEFAULT_PARAM_NAME_PROVIDER, config)
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
    MongockConfiguration config = new MongockConfiguration();
    config.setServiceIdentifier("myService");
    config.setTrackIgnored(false);
    new MigrationExecutor(driver, new DependencyManager(), DEFAULT_PARAM_NAME_PROVIDER, config)
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
      MongockConfiguration config = new MongockConfiguration();
      config.setServiceIdentifier("myService");
      config.setTrackIgnored(false);
      new MigrationExecutor(driver, new DependencyManager(), DEFAULT_PARAM_NAME_PROVIDER, config)
          .executeMigration(createInitialChangeLogs(ExecutorChangeLog.class));
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
    new MigrationExecutor(driver, new DependencyManager(), DEFAULT_PARAM_NAME_PROVIDER, config)
        .executeMigration(createInitialChangeLogs(ExecutorChangeLog.class));
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
    new MigrationExecutor(driver, new DependencyManager(), DEFAULT_PARAM_NAME_PROVIDER, config)
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
      new MigrationExecutor(driver, new DependencyManager(), DEFAULT_PARAM_NAME_PROVIDER, config)
          .executeMigration(createInitialChangeLogs(ExecutorWithChangeLogNonFailFastChangeLog1.class));
    } catch (Exception ex) {
    }

    assertTrue("Changelog's (1) methods have not been fully executed", ExecutorWithChangeLogNonFailFastChangeLog1.latch.await(1, TimeUnit.NANOSECONDS));
    assertTrue("Changelog's (2) methods have not been fully executed", ExecutorWithChangeLogNonFailFastChangeLog2.latch.await(1, TimeUnit.NANOSECONDS));

    // then
    ArgumentCaptor<ChangeEntry> captor = ArgumentCaptor.forClass(ChangeEntry.class);
    verify(changeEntryService, new Times(4)).save(captor.capture());

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
      new MigrationExecutor(driver, new DependencyManager(), DEFAULT_PARAM_NAME_PROVIDER, config)
          .executeMigration(createInitialChangeLogs(ExecutorWithChangeLogFailFastChangeLog1.class));
    } catch (Exception ex) {
    }

    assertTrue("Changelog's methods have not been fully executed", ExecutorWithChangeLogFailFastChangeLog1.latch.await(1, TimeUnit.NANOSECONDS));

    // then
    ArgumentCaptor<ChangeEntry> captor = ArgumentCaptor.forClass(ChangeEntry.class);
    verify(changeEntryService, new Times(2)).save(captor.capture());

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
  public void shouldThrowException_IfChangeSetParameterfNotInterface() {
    // given
    when(changeEntryService.isAlreadyExecuted("newChangeSet", "executor")).thenReturn(false);
    when(changeEntryService.isAlreadyExecuted("withNonLockGuardedParameter", "executor")).thenReturn(true);

    // then
    exceptionExpected.expect(MongockException.class);
    exceptionExpected.expectMessage("Error in method[ExecutorChangeLog.newChangeSet] : Parameter of type [DummyDependencyClass] must be an interface or be annotated with @NonLockGuarded");

    // when
    DependencyManager dependencyManager = new DependencyManager()
        .setLockGuardProxyFactory(new LockGuardProxyFactory(Mockito.mock(LockManager.class)))
        .addStandardDependency(new ChangeSetDependency(new DummyDependencyClass()));
    MongockConfiguration config = new MongockConfiguration();
    config.setServiceIdentifier("myService");
    config.setTrackIgnored(false);
    new MigrationExecutor(driver, dependencyManager, DEFAULT_PARAM_NAME_PROVIDER, config)
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

    MongockConfiguration config = new MongockConfiguration();
    config.setServiceIdentifier("myService");
    config.setTrackIgnored(false);
    new MigrationExecutor(driver, dependencyManager, DEFAULT_PARAM_NAME_PROVIDER, config)
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

    MongockConfiguration config = new MongockConfiguration();
    config.setServiceIdentifier("myService");
    config.setTrackIgnored(false);
    new MigrationExecutor(driver, dependencyManager, DEFAULT_PARAM_NAME_PROVIDER, config)
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

    MongockConfiguration config = new MongockConfiguration();
    config.setServiceIdentifier("myService");
    config.setTrackIgnored(false);
    new MigrationExecutor(driver, dependencyManager, DEFAULT_PARAM_NAME_PROVIDER, config)
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

    MongockConfiguration config = new MongockConfiguration();
    config.setServiceIdentifier("myService");
    config.setTrackIgnored(false);
    new MigrationExecutor(driver, dependencyManager, DEFAULT_PARAM_NAME_PROVIDER, config)
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
    new MigrationExecutor(driver, dependencyManager, DEFAULT_PARAM_NAME_PROVIDER, config)
        .executeMigration(createInitialChangeLogs(LegacyMigrationChangeLog.class));

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
    new MigrationExecutor(driver, new DependencyManager(), DEFAULT_PARAM_NAME_PROVIDER, config)
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
    MongockConfiguration config = new MongockConfiguration();
    config.setServiceIdentifier("myService");
    config.setTrackIgnored(false);
    new MigrationExecutor(driver, new DependencyManager(), DEFAULT_PARAM_NAME_PROVIDER, config)
        .executeMigration(createInitialChangeLogs(ChangeLogAlreadyExecuted.class));

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
    new MigrationExecutor(driver, new DependencyManager(), DEFAULT_PARAM_NAME_PROVIDER, config)
        .executeMigration(createInitialChangeLogs(ChangeLogAlreadyExecutedRunAlways.class));

    //then
    verify(lockManager, new Times(1)).acquireLockDefault();

    ArgumentCaptor<ChangeEntry> changeEntryCaptor = ArgumentCaptor.forClass(ChangeEntry.class);
    // ChangeEntry for ChangeSet "alreadyExecutedRunAlways" should be stored
    verify(changeEntryService, new Times(1)).save(changeEntryCaptor.capture());
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
    new MigrationExecutor(driver, new DependencyManager(), DEFAULT_PARAM_NAME_PROVIDER, config)
        .executeMigration(createInitialChangeLogs(ChangeLogAlreadyExecutedRunAlways.class));

    //then
    verify(lockManager, new Times(1)).acquireLockDefault();

    ArgumentCaptor<ChangeEntry> changeEntryCaptor = ArgumentCaptor.forClass(ChangeEntry.class);
    // ChangeEntry for ChangeSet "alreadyExecutedRunAlways" should not be stored
    verify(changeEntryService, new Times(0)).save(changeEntryCaptor.capture());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void shouldExecuteChangeLogBefore_whenPreMigration_ifTransactionPerMigration() {
    // given
    when(transactionableDriver.getLockManager()).thenReturn(lockManager);

    // when
    MongockConfiguration config = new MongockConfiguration();
    config.setServiceIdentifier("myService");
    config.setTrackIgnored(false);
    new MigrationExecutor(driver, new DependencyManager(), DEFAULT_PARAM_NAME_PROVIDER, config)
        .executeMigration(createInitialChangeLogs(ChangeLogPreMigration.class));

    // then
    verify(lockManager, new Times(1)).acquireLockDefault();

    ArgumentCaptor<ChangeEntry> changeEntryCaptor = ArgumentCaptor.forClass(ChangeEntry.class);
    // ChangeEntry for all ChangeSets should be stored (4)
    verify(changeEntryService, new Times(4)).save(changeEntryCaptor.capture());
    List<ChangeEntry> changeEntries = changeEntryCaptor.getAllValues();
    assertEquals(changeEntries.size(), 4);
    // Check invocations order
    assertEquals(changeEntries.get(0).getChangeId(), "preMigration1");
    assertEquals(changeEntries.get(1).getChangeId(), "preMigration2");
    assertEquals(changeEntries.get(2).getChangeId(), "standard1");
    assertEquals(changeEntries.get(3).getChangeId(), "standard2");
  }

  @Test
  @SuppressWarnings("unchecked")
  public void shouldExecuteChangeLogBefore_whenPreMigration_ifNoTransaction() {
    // given
    when(transactionableDriver.getLockManager()).thenReturn(lockManager);

    // when
    MongockConfiguration config = new MongockConfiguration();
    config.setServiceIdentifier("myService");
    config.setTrackIgnored(false);
    new MigrationExecutor(driver, new DependencyManager(), DEFAULT_PARAM_NAME_PROVIDER, config)
        .executeMigration(createInitialChangeLogs(ChangeLogPreMigration.class));

    // then
    verify(lockManager, new Times(1)).acquireLockDefault();

    ArgumentCaptor<ChangeEntry> changeEntryCaptor = ArgumentCaptor.forClass(ChangeEntry.class);
    // ChangeEntry for all ChangeSets should be stored (4)
    verify(changeEntryService, new Times(4)).save(changeEntryCaptor.capture());
    List<ChangeEntry> changeEntries = changeEntryCaptor.getAllValues();
    assertEquals(changeEntries.size(), 4);
    // Check invocations order
    assertEquals(changeEntries.get(0).getChangeId(), "preMigration1");
    assertEquals(changeEntries.get(1).getChangeId(), "preMigration2");
    assertEquals(changeEntries.get(2).getChangeId(), "standard1");
    assertEquals(changeEntries.get(3).getChangeId(), "standard2");
  }

  @Test
  @SuppressWarnings("unchecked")
  public void shouldExecuteChangeLogAfter_whenPostMigration_ifTransactionPerMigration() {
    // given
    when(transactionableDriver.getLockManager()).thenReturn(lockManager);

    // when
    MongockConfiguration config = new MongockConfiguration();
    config.setServiceIdentifier("myService");
    config.setTrackIgnored(false);
    new MigrationExecutor(driver, new DependencyManager(), DEFAULT_PARAM_NAME_PROVIDER, config)
        .executeMigration(createInitialChangeLogs(ChangeLogPostMigration.class));

    // then
    verify(lockManager, new Times(1)).acquireLockDefault();

    ArgumentCaptor<ChangeEntry> changeEntryCaptor = ArgumentCaptor.forClass(ChangeEntry.class);
    // ChangeEntry for all ChangeSets should be stored (4)
    verify(changeEntryService, new Times(4)).save(changeEntryCaptor.capture());
    List<ChangeEntry> changeEntries = changeEntryCaptor.getAllValues();
    assertEquals(changeEntries.size(), 4);
    // Check invocations order
    assertEquals(changeEntries.get(0).getChangeId(), "standard1");
    assertEquals(changeEntries.get(1).getChangeId(), "standard2");
    assertEquals(changeEntries.get(2).getChangeId(), "postMigration1");
    assertEquals(changeEntries.get(3).getChangeId(), "postMigration2");
  }

  @Test
  @SuppressWarnings("unchecked")
  public void shouldExecuteChangeLogAfter_whenPostMigration_ifNoTransaction() {
    // given
    when(transactionableDriver.getLockManager()).thenReturn(lockManager);

    // when
    MongockConfiguration config = new MongockConfiguration();
    config.setServiceIdentifier("myService");
    config.setTrackIgnored(false);
    new MigrationExecutor(driver, new DependencyManager(), DEFAULT_PARAM_NAME_PROVIDER, config)
        .executeMigration(createInitialChangeLogs(ChangeLogPostMigration.class));

    // then
    verify(lockManager, new Times(1)).acquireLockDefault();

    ArgumentCaptor<ChangeEntry> changeEntryCaptor = ArgumentCaptor.forClass(ChangeEntry.class);
    // ChangeEntry for all ChangeSets should be stored (4)
    verify(changeEntryService, new Times(4)).save(changeEntryCaptor.capture());
    List<ChangeEntry> changeEntries = changeEntryCaptor.getAllValues();
    assertEquals(changeEntries.size(), 4);
    // Check invocations order
    assertEquals(changeEntries.get(0).getChangeId(), "standard1");
    assertEquals(changeEntries.get(1).getChangeId(), "standard2");
    assertEquals(changeEntries.get(2).getChangeId(), "postMigration1");
    assertEquals(changeEntries.get(3).getChangeId(), "postMigration2");
  }

  @Test
  @SuppressWarnings("unchecked")
  public void shouldThrowException_IfChangeLogIsAnnotatedWithPreAndPostMigration() {
    // given
    when(driver.getLockManager()).thenReturn(lockManager);

    // then
    exceptionExpected.expect(MongockException.class);
    exceptionExpected.expectMessage("A ChangeLog can't be defined to be executed pre and post migration.");

    // when
    MongockConfiguration config = new MongockConfiguration();
    config.setServiceIdentifier("myService");
    config.setTrackIgnored(false);
    new MigrationExecutor(driver, new DependencyManager(), DEFAULT_PARAM_NAME_PROVIDER, config)
        .executeMigration(createInitialChangeLogs(ChangeLogPrePostMigration.class));
  }

  private SortedSet<ChangeLogItem> createInitialChangeLogs(Class<?> executorChangeLogClass) {
    return new ChangeLogService(Collections.singletonList(executorChangeLogClass.getPackage().getName()), Collections.emptyList(), "0", String.valueOf(Integer.MAX_VALUE))
        .fetchChangeLogs();
  }

  private void injectDummyDependency(Class<?> type, Object instance) {
    Set<ChangeSetDependency> dependencies = new HashSet<>();
    dependencies.add(new ChangeSetDependency(type, instance));
    when(driver.getDependencies()).thenReturn(dependencies);
  }

  private abstract class TransactionableConnectionDriver implements ConnectionDriver {
  }
}
