package io.cloudyrock.changock.runner.spring.v5;


import io.changock.driver.api.driver.ChangeSetDependency;
import io.changock.driver.api.driver.ConnectionDriver;
import io.changock.driver.api.driver.ForbiddenParametersMap;
import io.changock.driver.api.entry.ChangeEntryService;
import io.changock.driver.api.lock.LockManager;
import io.changock.migration.api.exception.ChangockException;
import io.changock.runner.core.DependencyManager;
import io.changock.runner.core.MigrationExecutor;
import io.changock.runner.spring.v5.ChangockSpring5;
import io.cloudyrock.changock.runner.spring.v5.profiles.enseuredecorators.EnsureDecoratorChangerLog;
import io.cloudyrock.changock.runner.spring.v5.profiles.integration.IntegrationProfiledChangerLog;
import io.cloudyrock.changock.runner.spring.v5.profiles.withForbiddenParameter.ChangeLogWithForbiddenParameter;
import io.cloudyrock.changock.runner.spring.v5.profiles.withForbiddenParameter.ForbiddenParameter;
import io.cloudyrock.changock.runner.spring.v5.profiles.withInterfaceParameter.ChangeLogWithInterfaceParameter;
import io.cloudyrock.changock.runner.spring.v5.util.CallVerifier;
import io.cloudyrock.changock.runner.spring.v5.util.ClassNotInterfaced;
import io.cloudyrock.changock.runner.spring.v5.util.InterfaceDependency;
import io.cloudyrock.changock.runner.spring.v5.util.InterfaceDependencyImpl;
import io.cloudyrock.changock.runner.spring.v5.util.InterfaceDependencyImplNoLockGarded;
import io.cloudyrock.changock.runner.spring.v5.util.MongockTemplateForTest;
import io.cloudyrock.changock.runner.spring.v5.util.MongockTemplateForTestImpl;
import io.cloudyrock.changock.runner.spring.v5.util.MongockTemplateForTestImplChild;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.internal.verification.Times;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SpringChangockApplicationRunnerTest {

  private ChangeEntryService changeEntryService;
  private LockManager lockManager;
  private ConnectionDriver driver;
  private CallVerifier callVerifier;

  @Rule
  public ExpectedException exceptionExpected = ExpectedException.none();
  private ApplicationContext springContext;

  @Before
  public void setUp() {
    lockManager = mock(LockManager.class);
    changeEntryService = mock(ChangeEntryService.class);
    driver = mock(ConnectionDriver.class);
    when(driver.getLockManager()).thenReturn(lockManager);
    when(driver.getLockManager()).thenReturn(lockManager);
    when(driver.getLockManager()).thenReturn(lockManager);
    when(driver.getChangeEntryService()).thenReturn(changeEntryService);
    ForbiddenParametersMap forbiddenParameters = new ForbiddenParametersMap();
    forbiddenParameters.put(ForbiddenParameter.class, String.class);
    when(driver.getForbiddenParameters()).thenReturn(forbiddenParameters);

    callVerifier = new CallVerifier();
    Set<ChangeSetDependency> dependencySet = new HashSet<>();
    dependencySet.add(new ChangeSetDependency(CallVerifier.class, callVerifier));
    when(driver.getDependencies()).thenReturn(dependencySet);


    Environment environment = mock(Environment.class);
    when(environment.getActiveProfiles()).thenReturn(new String[]{"profileIncluded1", "profileIncluded2"});
    springContext = mock(ApplicationContext.class);
    when(springContext.getEnvironment()).thenReturn(environment);
    when(springContext.getBean(Environment.class)).thenReturn(environment);
    when(springContext.getBean(MongockTemplateForTest.class)).thenReturn(new MongockTemplateForTestImpl());
    when(springContext.getBean(InterfaceDependency.class)).thenReturn(new InterfaceDependencyImpl());
    when(springContext.getBean(ClassNotInterfaced.class)).thenReturn(new ClassNotInterfaced());
  }

  @Test
  public void shouldRunOnlyProfiledChangeSets() {

    // when
    buildAndRun(IntegrationProfiledChangerLog.class.getPackage().getName());

    // then
    ArgumentCaptor<String> changeSetIdCaptor = ArgumentCaptor.forClass(String.class);
    verify(changeEntryService, new Times(3)).isAlreadyExecuted(changeSetIdCaptor.capture(), anyString());

    List<String> changeSetIdList = changeSetIdCaptor.getAllValues();
    assertEquals(3, changeSetIdList.size());
    assertTrue(changeSetIdList.contains("testWithProfileIncluded1"));
    assertTrue(changeSetIdList.contains("testWithProfileIncluded2"));
    assertTrue(changeSetIdList.contains("testWithProfileIncluded1OrProfileINotIncluded"));
  }

  @Test
  public void shouldInjectEnvironmentToChangeSet() {

    // when
    buildAndRun(IntegrationProfiledChangerLog.class.getPackage().getName());

    // then
    assertEquals(1, callVerifier.counter);
  }

  @Test
  public void shouldPrioritizeConnectorDependenciesOverContext() {
    // given
    when(changeEntryService.isAlreadyExecuted("ensureDecoratorChangeSet", "testuser")).thenReturn(false);
    callVerifier = new CallVerifier();
    Set<ChangeSetDependency> dependencySet = new HashSet<>();
    dependencySet.add(new ChangeSetDependency(CallVerifier.class, callVerifier));
    dependencySet.add(new ChangeSetDependency(MongockTemplateForTestImpl.class, new MongockTemplateForTestImplChild()));
    when(driver.getDependencies()).thenReturn(dependencySet);

    Environment environment = mock(Environment.class);
    springContext = mock(ApplicationContext.class);
    when(springContext.getEnvironment()).thenReturn(environment);
    when(springContext.getBean(Environment.class)).thenReturn(environment);
    when(springContext.getBean(MongockTemplateForTestImpl.class)).thenReturn(new MongockTemplateForTestImpl());

    // when
    buildAndRun(EnsureDecoratorChangerLog.class.getPackage().getName());

    // then
    assertEquals(1, callVerifier.counter);
  }

  @Test
  public void shouldFail_IfSpringContextNotInjected() {

    exceptionExpected.expect(ChangockException.class);
    exceptionExpected.expectMessage("ApplicationContext from Spring must be injected to Builder");

    ChangockSpring5.builder()
        .setDriver(driver)
        .addChangeLogsScanPackage(IntegrationProfiledChangerLog.class.getPackage().getName())
        .buildApplicationRunner()
        .run(null);
  }

  @Test
  public void shouldFail_whenRunningChangeSet_ifForbiddenParameterFromDriver() {

    when(changeEntryService.isAlreadyExecuted("withForbiddenParameter", "executor")).thenReturn(true);

    // then
    exceptionExpected.expect(ChangockException.class);
    exceptionExpected.expectMessage("Error in method[ChangeLogWithForbiddenParameter.withForbiddenParameter] : Forbidden parameter[ForbiddenParameter]. Must be replaced with [String]");

    // when
    buildAndRun(ChangeLogWithForbiddenParameter.class.getPackage().getName());
  }





  @Test
  public void shouldThrowException_IfChangeSetParameterfNotInterface() {
    // given
    when(changeEntryService.isAlreadyExecuted("withInterfaceParameter", "executor")).thenReturn(true);
    when(changeEntryService.isAlreadyExecuted("withInterfaceParameter2", "executor")).thenReturn(true);
    when(changeEntryService.isAlreadyExecuted("withClassNotInterfacedParameter", "executor")).thenReturn(false);

    // then
    exceptionExpected.expect(ChangockException.class);
    exceptionExpected.expectMessage("Error in method[ChangeLogWithInterfaceParameter.withClassNotInterfacedParameter] : Parameter of type [ClassNotInterfaced] must be an interface");

    // when
    buildAndRun(ChangeLogWithInterfaceParameter.class.getPackage().getName());
  }

  @Test
  public void shouldReturnProxy_IfStandardDependency() {
    // given
    when(changeEntryService.isAlreadyExecuted("withInterfaceParameter", "executor")).thenReturn(false);
    when(changeEntryService.isAlreadyExecuted("withInterfaceParameter2", "executor")).thenReturn(true);
    when(changeEntryService.isAlreadyExecuted("withClassNotInterfacedParameter", "executor")).thenReturn(true);


    // when
    buildAndRun(ChangeLogWithInterfaceParameter.class.getPackage().getName());

    // then
    verify(lockManager, new Times(1)).ensureLockDefault();
  }

  @Test
  public void proxyReturnedShouldReturnAProxy_whenCallingAMethod_IfInterface() {
    // given
    when(changeEntryService.isAlreadyExecuted("withInterfaceParameter", "executor")).thenReturn(true);
    when(changeEntryService.isAlreadyExecuted("withInterfaceParameter2", "executor")).thenReturn(false);
    when(changeEntryService.isAlreadyExecuted("withClassNotInterfacedParameter", "executor")).thenReturn(true);

    // when
    buildAndRun(ChangeLogWithInterfaceParameter.class.getPackage().getName());

    // then
    verify(lockManager, new Times(2)).ensureLockDefault();
  }


  @Test
  public void shouldNotReturnProxy_IfClassAnnotatedWithNonLockGuarded() {
    // given
    when(changeEntryService.isAlreadyExecuted("withInterfaceParameter", "executor")).thenReturn(false);
    when(changeEntryService.isAlreadyExecuted("withInterfaceParameter2", "executor")).thenReturn(true);
    when(changeEntryService.isAlreadyExecuted("withClassNotInterfacedParameter", "executor")).thenReturn(true);
    when(springContext.getBean(InterfaceDependency.class)).thenReturn(new InterfaceDependencyImplNoLockGarded());


    // when
    buildAndRun(ChangeLogWithInterfaceParameter.class.getPackage().getName());

    // then
    verify(lockManager, new Times(0)).ensureLockDefault();
  }

  @Test
  public void shouldNotReturnProxy_IfParameterAnnotatedWithNonLockGuarded() {
    // given
    when(changeEntryService.isAlreadyExecuted("withInterfaceParameter", "executor")).thenReturn(true);
    when(changeEntryService.isAlreadyExecuted("withInterfaceParameter2", "executor")).thenReturn(true);
    when(changeEntryService.isAlreadyExecuted("withClassNotInterfacedParameter", "executor")).thenReturn(true);
    when(changeEntryService.isAlreadyExecuted("withNonLockGuardedParameter", "executor")).thenReturn(false);


    // when
    buildAndRun(ChangeLogWithInterfaceParameter.class.getPackage().getName());

    // then
    verify(lockManager, new Times(0)).ensureLockDefault();
  }

  private void buildAndRun(String packageName) {
    ChangockSpring5.builder()
        .setDriver(driver)
        .addChangeLogsScanPackage(packageName)
        .setSpringContext(springContext)
        .buildApplicationRunner()
        .run(null);
  }
}
