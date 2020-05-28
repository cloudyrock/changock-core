package io.changock.runner.core.builder;

import io.changock.driver.api.driver.ConnectionDriver;
import io.changock.runner.core.ChangockBase;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.verification.Times;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

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

  @Test
  public void shouldAssignAllTheParameters() {
    new DummyRunnerBuilder()
        .setDriver(driver)
        .setLockConfig(1, 2, 3)
        .setEnabled(false)
        .setStartSystemVersion("start")
        .setEndSystemVersion("end")
        .setThrowExceptionIfCannotObtainLock(false)
        .addChangeLogsScanPackage("package")
        .withMetadata(metadata)
        .validate();
  }

  @Test
  public void shouldCallAllTheMethods_whenSetConfig() {

    DummyRunnerBuilder builder = Mockito.spy(new DummyRunnerBuilder().setDriver(driver));
    builder.setConfig(getConfig(false));
    checkStandardBuilderCalls(builder);
    verify(builder, new Times(1)).setThrowExceptionIfCannotObtainLock(false);
  }

  @Test
  public void shouldThrowExceptionTrueByDefault() {

    DummyRunnerBuilder builder = Mockito.spy(new DummyRunnerBuilder().setDriver(driver));
    builder.setConfig(getConfig(null));
    checkStandardBuilderCalls(builder);
    verify(builder, new Times(1)).setThrowExceptionIfCannotObtainLock(true);
  }

  private void checkStandardBuilderCalls(DummyRunnerBuilder builder) {
    verify(builder, new Times(1)).addChangeLogsScanPackage(PACKAGE_PATH);
    verify(builder, new Times(1)).setLockConfig(LOCK_ACQ_MIN, MAX_WAIT_LOCK, MAX_TRIES);
    verify(builder, new Times(1)).setEnabled(false);
    verify(builder, new Times(1)).setStartSystemVersion(START_SYSTEM_VERSION);
    verify(builder, new Times(1)).setEndSystemVersion(END_SYSTEM_VERSION);
    verify(builder, new Times(1)).withMetadata(METADATA);
  }

  private ChangockConfiguration getConfig(Boolean throwEx) {
    ChangockConfiguration config = new ChangockConfiguration() {
    };
    config.setChangeLogsScanPackage(PACKAGE_PATH);
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


class DummyRunnerBuilder extends RunnerBuilderBase<DummyRunnerBuilder, ConnectionDriver, ChangockConfiguration> {


  void validate() {
    assertEquals(driver, this.driver);
    assertEquals(lockAcquiredForMinutes, 1);
    assertEquals(maxWaitingForLockMinutes, 2);
    assertEquals(maxTries, 3);
    assertFalse(this.enabled);
    assertEquals("start", this.startSystemVersion);
    assertEquals("end", this.endSystemVersion);
    assertFalse(this.throwExceptionIfCannotObtainLock);
    assertEquals("package", this.changeLogsScanPackage);
    assertEquals(metadata, this.metadata);
  }

  @Override
  protected DummyRunnerBuilder returnInstance() {
    return this;
  }


  public ChangockBase build() {
    return null;
  }
}
