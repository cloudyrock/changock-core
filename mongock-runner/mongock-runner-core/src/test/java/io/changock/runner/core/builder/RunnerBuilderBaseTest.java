package io.changock.runner.core.builder;

import io.changock.driver.api.driver.ConnectionDriver;
import com.github.cloudyrock.mongock.ChangeLogItem;
import com.github.cloudyrock.mongock.ChangeSetItem;
import com.github.cloudyrock.mongock.ChangeLog;
import io.changock.runner.core.executor.ChangeLogService;
import io.changock.runner.core.executor.ChangockBase;
import io.changock.runner.core.event.EventPublisher;
import io.changock.runner.core.executor.MigrationExecutor;
import com.github.cloudyrock.mongock.config.MongockConfiguration;
import com.github.cloudyrock.mongock.config.LegacyMigration;
import io.changock.runner.core.changelogs.test1.ChangeLogSuccess11;
import io.changock.runner.core.changelogs.test1.ChangeLogSuccess12;
import io.changock.runner.core.util.LegacyMigrationDummyImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.internal.verification.Times;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RunnerBuilderBaseTest {

  private static final String PACKAGE_PATH = "package";
  private static final String START_SYSTEM_VERSION = "start_system_version";
  private static final String END_SYSTEM_VERSION = "end_system_versions";
  private static final int LOCK_ACQ_MIN = 100;
  private static final int MAX_WAIT_LOCK = 200;
  private static final int MAX_TRIES = 300;
  private static final Map<String, Object> METADATA = new HashMap<>();
  ConnectionDriver driver = mock(ConnectionDriver.class);
  Map<String, Object> metadata = new HashMap<>();

  @Before
  public void before() {
    when(driver.getLegacyMigrationChangeLogClass(Mockito.anyBoolean())).thenReturn(DummyRunnerBuilder.LegacyMigrationChangeLogDummy.class);
  }

  @Test
  public void shouldAssignAllTheParameters() {
    new DummyRunnerBuilder()
        .setDriver(driver)
        .setEnabled(false)
        .setStartSystemVersion("start")
        .setEndSystemVersion("end")
        .dontFailIfCannotAcquireLock()
        .addChangeLogsScanPackage("package")
        .withMetadata(metadata)
        .validate();
  }

  @Test
  public void shouldCallAllTheMethods_whenSetConfig() {

    DummyRunnerBuilder builder = Mockito.spy(new DummyRunnerBuilder().setDriver(driver));
    builder.setConfig(getConfig(false, PACKAGE_PATH));
    checkStandardBuilderCalls(builder);
    verify(builder, new Times(1)).dontFailIfCannotAcquireLock();
  }

  @Test
  public void shouldThrowExceptionTrueByDefault() {

    DummyRunnerBuilder builder = Mockito.spy(new DummyRunnerBuilder().setDriver(driver));
    builder.setConfig(getConfig(null, PACKAGE_PATH));
    checkStandardBuilderCalls(builder);
    verify(builder, new Times(0)).dontFailIfCannotAcquireLock();
  }


  /**
   * SCAN PACKAGES
   */
  @Test
  public void shouldAddMultiplePackages_whenAddingList() {
    DummyRunnerBuilder builder = Mockito.spy(new DummyRunnerBuilder().setDriver(driver));
    builder.addChangeLogsScanPackage("package1");
    builder.addChangeLogsScanPackage("package2");
    builder.addChangeLogsScanPackage("package3");
    verify(builder, new Times(1)).addChangeLogsScanPackages(Collections.singletonList("package1"));
    verify(builder, new Times(1)).addChangeLogsScanPackages(Collections.singletonList("package2"));
    verify(builder, new Times(1)).addChangeLogsScanPackages(Collections.singletonList("package3"));
  }

  @Test
  public void shouldAddSingleClass() {
    MigrationExecutor executor = mock(MigrationExecutor.class);
    new DummyRunnerBuilder()
        .setDriver(driver)
        .setExecutor(executor)
        .addChangeLogClass(ChangeLogSuccess11.class)
        .build()
        .execute();


    ArgumentCaptor<SortedSet<ChangeLogItem>> packageCaptors = ArgumentCaptor.forClass(SortedSet.class);
    verify(executor, new Times(1)).executeMigration(packageCaptors.capture());

    ChangeLogItem  changeLogItem = new ArrayList<>(packageCaptors.getValue()).get(0);
    assertEquals(ChangeLogSuccess11.class, changeLogItem.getType());
    assertEquals("1", changeLogItem.getOrder());

    ChangeSetItem changeSetItem = changeLogItem.getChangeSetElements().get(0);
    assertEquals("ChangeSet_121", changeSetItem.getId());
    assertEquals("testUser11", changeSetItem.getAuthor());
    assertEquals("1", changeSetItem.getOrder());
    assertTrue(changeSetItem.isRunAlways());
    assertEquals("1", changeSetItem.getSystemVersion());
    assertEquals("method_111", changeSetItem.getMethod().getName());
    assertTrue(changeSetItem.isFailFast());

  }


  @Test
  public void shouldNotDuplicateWhenAddingSingleClassIfTwice() {
    MigrationExecutor executor = mock(MigrationExecutor.class);
    new DummyRunnerBuilder()
        .setDriver(driver)
        .setExecutor(executor)
        .addChangeLogClass(ChangeLogSuccess11.class)
        .addChangeLogClass(ChangeLogSuccess11.class)
        .build()
        .execute();

    ArgumentCaptor<SortedSet<ChangeLogItem>> packageCaptors = ArgumentCaptor.forClass(SortedSet.class);
    verify(executor, new Times(1)).executeMigration(packageCaptors.capture());

    assertEquals(1, new ArrayList<>(new ArrayList<>(packageCaptors.getValue())).size());

  }


  @Test
  public void shouldAddClassAndPackage() {
    MigrationExecutor executor = mock(MigrationExecutor.class);
    new DummyRunnerBuilder()
        .setDriver(driver)
        .setExecutor(executor)
        .addChangeLogClass(ChangeLogSuccess11.class)
        .addChangeLogsScanPackage(ChangeLogSuccess11.class.getPackage().getName())
        .build()
        .execute();

    ArgumentCaptor<SortedSet<ChangeLogItem>> packageCaptors = ArgumentCaptor.forClass(SortedSet.class);
    verify(executor, new Times(1)).executeMigration(packageCaptors.capture());

    ArrayList<ChangeLogItem> changeLogItemsList = new ArrayList<>(new ArrayList<>(packageCaptors.getValue()));
    assertEquals(2, changeLogItemsList.size());

    ChangeLogItem  changeLogItem = new ArrayList<>(packageCaptors.getValue()).get(0);
    assertEquals(ChangeLogSuccess11.class, changeLogItem.getType());
    assertEquals("1", changeLogItem.getOrder());

    ChangeLogItem  changeLogItem2 = new ArrayList<>(packageCaptors.getValue()).get(1);
    assertEquals(ChangeLogSuccess12.class, changeLogItem2.getType());
    assertEquals("2", changeLogItem2.getOrder());
  }

  @Test
  public void shouldAddMultiplePackages_whenMultiplePackagesFromConfig() {
    DummyRunnerBuilder builder = Mockito.spy(new DummyRunnerBuilder().setDriver(driver));
    builder.setConfig(getConfig(null, "package1", "package2"));
    verify(builder, new Times(1)).addChangeLogsScanPackage("package1");
    verify(builder, new Times(1)).addChangeLogsScanPackage("package2");
  }

  private void checkStandardBuilderCalls(DummyRunnerBuilder builder) {
    verify(builder, new Times(1)).addChangeLogsScanPackages(Collections.singletonList(PACKAGE_PATH));
    verify(builder, new Times(1)).setEnabled(false);
    verify(builder, new Times(1)).setStartSystemVersion(START_SYSTEM_VERSION);
    verify(builder, new Times(1)).setEndSystemVersion(END_SYSTEM_VERSION);
    verify(builder, new Times(1)).withMetadata(METADATA);
  }

  private MongockConfiguration getConfig(Boolean throwEx, String... packages) {
    MongockConfiguration config = new DummyMongockConfiguration();
    config.setChangeLogsScanPackage(Arrays.asList(packages));
    config.setEnabled(false);
    config.setStartSystemVersion(START_SYSTEM_VERSION);
    config.setEndSystemVersion(END_SYSTEM_VERSION);
    config.setLockAcquiredForMinutes(LOCK_ACQ_MIN);
    config.setMaxWaitingForLockMinutes(MAX_WAIT_LOCK);
    config.setMaxTries(MAX_TRIES);
    config.setMetadata(METADATA);
    if (throwEx != null) {
      config.setThrowExceptionIfCannotObtainLock(throwEx);
    }
    return config;
  }
}

class DummyMongockConfiguration extends MongockConfiguration{

  @Override
  public LegacyMigration getLegacyMigration() {
    return new LegacyMigrationDummyImpl();
  }

  @Override
  protected String getLockRepositoryNameDefault() {
    return "lockRepositoryName";
  }

  @Override
  protected String getChangeLogRepositoryNameDefault() {
    return "changeLogRepositoryName";
  }
}

class DummyRunnerBuilder extends RunnerBuilderBase<DummyRunnerBuilder, ConnectionDriver, MongockConfiguration> {


  private MigrationExecutor executor;

  void validate() {
    assertEquals(driver, this.driver);
    assertFalse(this.enabled);
    assertEquals("start", this.startSystemVersion);
    assertEquals("end", this.endSystemVersion);
    assertFalse(this.throwExceptionIfCannotObtainLock);
    assertEquals(1, this.changeLogsScanPackage.size());
    assertTrue(changeLogsScanPackage.contains("package"));
    assertEquals(metadata, this.metadata);
  }

  @Override
  protected DummyRunnerBuilder returnInstance() {
    return this;
  }

  public DummyRunnerBuilder setExecutor(MigrationExecutor executor) {
    this.executor = executor;
    return this;
  }

  public ChangockBase build() {
    return new DummyRunner(
        executor != null ? executor : buildExecutorDefault(),
        buildChangeLogServiceDefault(),
        throwExceptionIfCannotObtainLock,
        enabled,
        mock(EventPublisher.class));

  }

  @ChangeLog
  public static class LegacyMigrationChangeLogDummy {

  }
}

class DummyRunner extends ChangockBase<MigrationExecutor> {
  DummyRunner(MigrationExecutor executor, ChangeLogService changeLogService, boolean throwExceptionIfCannotObtainLock, boolean enabled, EventPublisher eventPublisher) {
    super(executor, changeLogService, throwExceptionIfCannotObtainLock, enabled, eventPublisher);
  }
}

