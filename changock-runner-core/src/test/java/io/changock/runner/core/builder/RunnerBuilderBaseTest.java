package io.changock.runner.core.builder;

import io.changock.driver.api.driver.ConnectionDriver;
import io.changock.runner.core.ChangockBase;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;

public class RunnerBuilderBaseTest {

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

}


class DummyRunnerBuilder extends RunnerBuilderBase<DummyRunnerBuilder, ChangockBase> {


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

  @Override
  public ChangockBase build() {
    return null;
  }
}
