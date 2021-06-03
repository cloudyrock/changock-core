package com.github.cloudyrock.mongock.runner.core.builder;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeLogItem;
import com.github.cloudyrock.mongock.ChangeSetItem;
import com.github.cloudyrock.mongock.config.MongockConfiguration;
import com.github.cloudyrock.mongock.driver.api.driver.ConnectionDriver;
import com.github.cloudyrock.mongock.runner.core.builder.roles.ChangeLogScanner;
import com.github.cloudyrock.mongock.runner.core.builder.roles.ChangeLogWriter;
import com.github.cloudyrock.mongock.runner.core.builder.roles.Configurable;
import com.github.cloudyrock.mongock.runner.core.builder.roles.DependencyInjectable;
import com.github.cloudyrock.mongock.runner.core.builder.roles.DriverConnectable;
import com.github.cloudyrock.mongock.runner.core.builder.roles.LegacyMigrator;
import com.github.cloudyrock.mongock.runner.core.builder.roles.RunnerBuilder;
import com.github.cloudyrock.mongock.runner.core.builder.roles.SelfInstanstiator;
import com.github.cloudyrock.mongock.runner.core.builder.roles.ServiceIdentificable;
import com.github.cloudyrock.mongock.runner.core.builder.roles.SystemVersionable;
import com.github.cloudyrock.mongock.runner.core.changelogs.test1.ChangeLogSuccess11;
import com.github.cloudyrock.mongock.runner.core.changelogs.test1.ChangeLogSuccess12;
import com.github.cloudyrock.mongock.runner.core.event.EventPublisher;
import com.github.cloudyrock.mongock.runner.core.executor.Executor;
import com.github.cloudyrock.mongock.runner.core.executor.ExecutorFactory;
import com.github.cloudyrock.mongock.runner.core.executor.MongockRunner;
import com.github.cloudyrock.mongock.runner.core.executor.MongockRunnerImpl;
import com.github.cloudyrock.mongock.runner.core.executor.dependency.DependencyManager;
import com.github.cloudyrock.mongock.runner.core.executor.operation.change.MigrationExecutor;
import com.github.cloudyrock.mongock.runner.core.executor.operation.change.MigrationOp;
import com.github.cloudyrock.mongock.runner.core.util.LegacyMigrationDummyImpl;
import com.github.cloudyrock.mongock.util.test.ReflectionUtils;
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
  private static final long LOCK_ACQ_MILLIS = 100 * 60 * 1000L;
  private static final long LOCK_TRY_FREQ_MILLIS = 1000L;
  private static final long LOCK_QUIT_TRY_MILLIS = 3 * 60 * 1000L;

  private static final Map<String, Object> METADATA = new HashMap<>();
  ConnectionDriver driver = mock(ConnectionDriver.class);
  Map<String, Object> metadata = new HashMap<>();

  @Before
  public void before() {
    when(driver.getLegacyMigrationChangeLogClass(Mockito.anyBoolean())).thenReturn(DummyRunnerBuilder.LegacyMigrationChangeLogDummy.class);
  }

  @Test
  public void shouldAssignAllTheParameters() {
    new DummyRunnerBuilder(new ExecutorFactory<>())
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

    DummyRunnerBuilder builder = Mockito.spy(new DummyRunnerBuilder(new ExecutorFactory<>()).setDriver(driver));
    MongockConfiguration expectedConfig = getConfig(false, PACKAGE_PATH);
    builder.setConfig(expectedConfig);
    //todo check all the properties are set rightly
    MongockConfiguration actualConfig = (MongockConfiguration)ReflectionUtils.getPrivateField(builder, RunnerBuilderBase.class, "config");
    assertEquals(expectedConfig, actualConfig);
  }

  @Test
  public void shouldThrowExceptionTrueByDefault() {

    DummyRunnerBuilder builder = Mockito.spy(new DummyRunnerBuilder(new ExecutorFactory<>()).setDriver(driver));
    builder.setConfig(getConfig(null, PACKAGE_PATH));
    checkStandardBuilderCalls(builder);
    verify(builder, new Times(0)).dontFailIfCannotAcquireLock();
  }


  /**
   * SCAN PACKAGES
   */
  @Test
  public void shouldAddMultiplePackages_whenAddingList() {
    DummyRunnerBuilder builder = Mockito.spy(new DummyRunnerBuilder(new ExecutorFactory<>()).setDriver(driver));
    builder.addChangeLogsScanPackage("package1");
    builder.addChangeLogsScanPackage("package2");
    builder.addChangeLogsScanPackage("package3");
    verify(builder, new Times(1)).addChangeLogsScanPackages(Collections.singletonList("package1"));
    verify(builder, new Times(1)).addChangeLogsScanPackages(Collections.singletonList("package2"));
    verify(builder, new Times(1)).addChangeLogsScanPackages(Collections.singletonList("package3"));
  }

  @Test
  public void shouldAddSingleClass() {
    Executor executor = mock(MigrationExecutor.class);
    new DummyRunnerBuilder(new ExecutorFactory<>())
        .setDriver(driver)
        .setExecutor(executor)
        .addChangeLogClass(ChangeLogSuccess11.class)
        .build()
        .execute();


    ArgumentCaptor<SortedSet<ChangeLogItem>> packageCaptors = ArgumentCaptor.forClass(SortedSet.class);
    verify(executor, new Times(1)).executeMigration(packageCaptors.capture());

    ChangeLogItem changeLogItem = new ArrayList<>(packageCaptors.getValue()).get(0);
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
    Executor executor = mock(MigrationExecutor.class);
    new DummyRunnerBuilder(new ExecutorFactory<>())
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
    Executor executor = mock(MigrationExecutor.class);
    new DummyRunnerBuilder(new ExecutorFactory<>())
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

    ChangeLogItem changeLogItem = new ArrayList<>(packageCaptors.getValue()).get(0);
    assertEquals(ChangeLogSuccess11.class, changeLogItem.getType());
    assertEquals("1", changeLogItem.getOrder());

    ChangeLogItem changeLogItem2 = new ArrayList<>(packageCaptors.getValue()).get(1);
    assertEquals(ChangeLogSuccess12.class, changeLogItem2.getType());
    assertEquals("2", changeLogItem2.getOrder());
  }

  @Test
  public void shouldAddMultiplePackages_whenMultiplePackagesFromConfig() {
    DummyRunnerBuilder builder = Mockito.spy(new DummyRunnerBuilder(new ExecutorFactory<>()).setDriver(driver));
    builder.setConfig(getConfig(null, "package1", "package2"));
    MongockConfiguration actualConfig = (MongockConfiguration)ReflectionUtils.getPrivateField(builder, RunnerBuilderBase.class, "config");
    assertTrue(actualConfig.getChangeLogsScanPackage().contains("package1"));
    assertTrue(actualConfig.getChangeLogsScanPackage().contains("package2"));
  }

  private void checkStandardBuilderCalls(DummyRunnerBuilder builder) {

    MongockConfiguration actualConfig = (MongockConfiguration)ReflectionUtils.getPrivateField(builder, RunnerBuilderBase.class, "config");

    assertTrue(actualConfig.getChangeLogsScanPackage().contains(PACKAGE_PATH) && actualConfig.getChangeLogsScanPackage().size() == 1);
    assertFalse(actualConfig.isEnabled());
    assertEquals(START_SYSTEM_VERSION, actualConfig.getStartSystemVersion());
    assertEquals(END_SYSTEM_VERSION, actualConfig.getEndSystemVersion());
    assertEquals(METADATA, actualConfig.getMetadata());
  }

  private MongockConfiguration getConfig(Boolean throwEx, String... packages) {
    MongockConfiguration config = new DummyMongockConfiguration();
    config.setChangeLogsScanPackage(Arrays.asList(packages));
    config.setEnabled(false);
    config.setStartSystemVersion(START_SYSTEM_VERSION);
    config.setEndSystemVersion(END_SYSTEM_VERSION);
    config.setMetadata(METADATA);

    config.setLockAcquiredForMillis(LOCK_ACQ_MILLIS);
    config.setLockTryFrequencyMillis(LOCK_TRY_FREQ_MILLIS);
    config.setLockQuitTryingAfterMillis(LOCK_QUIT_TRY_MILLIS);

    if (throwEx != null) {
      config.setThrowExceptionIfCannotObtainLock(throwEx);
    }
    return config;
  }
}

class DummyMongockConfiguration extends MongockConfiguration {

  public DummyMongockConfiguration() {
    this.setLegacyMigration(new LegacyMigrationDummyImpl());
    this.setLockRepositoryName("lockRepositoryName");
    this.setChangeLogRepositoryName("changeLogRepositoryName");
  }

}

class DummyRunnerBuilder extends RunnerBuilderBase<DummyRunnerBuilder, Boolean, MongockConfiguration>
implements
    ChangeLogScanner<DummyRunnerBuilder, MongockConfiguration>,
    ChangeLogWriter<DummyRunnerBuilder, MongockConfiguration>,
    LegacyMigrator<DummyRunnerBuilder, MongockConfiguration>,
    DriverConnectable<DummyRunnerBuilder, MongockConfiguration>,
    Configurable<DummyRunnerBuilder, MongockConfiguration>,
    SystemVersionable<DummyRunnerBuilder, MongockConfiguration>,
    DependencyInjectable<DummyRunnerBuilder>,
    ServiceIdentificable<DummyRunnerBuilder, MongockConfiguration>,
    RunnerBuilder<DummyRunnerBuilder, Boolean, MongockConfiguration>,
    SelfInstanstiator<DummyRunnerBuilder> {


  private Executor executor;

  protected DummyRunnerBuilder(ExecutorFactory<MongockConfiguration> executorFactory) {
    super(new MigrationOp(), executorFactory, new MongockConfiguration(), new DependencyManager());
  }

  void validate() {
    assertEquals(driver, this.driver);
    assertFalse(config.isEnabled());
    assertEquals("start", config.getStartSystemVersion());
    assertEquals("end", config.getEndSystemVersion());
    assertFalse(config.isThrowExceptionIfCannotObtainLock());
    assertEquals(1, this.config.getChangeLogsScanPackage().size());
    assertTrue(config.getChangeLogsScanPackage().contains("package"));
  }

  @Override
  protected void beforeBuildRunner() {
  }


  @Override
  public DummyRunnerBuilder getInstance() {
    return this;
  }


  public DummyRunnerBuilder setExecutor(Executor executor) {
    this.executor = executor;
    return this;
  }

  public MongockRunner<Boolean> build() {
    return new MongockRunnerImpl<>(
        executor != null ? executor : buildExecutor(driver),
        buildChangeLogService(),
        config.isThrowExceptionIfCannotObtainLock(),
        config.isEnabled(),
        mock(EventPublisher.class));

  }

  @ChangeLog
  public static class LegacyMigrationChangeLogDummy {

  }
}


