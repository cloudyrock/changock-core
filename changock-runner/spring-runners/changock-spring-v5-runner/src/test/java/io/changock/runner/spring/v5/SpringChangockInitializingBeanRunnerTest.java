package io.changock.runner.spring.v5;


import io.changock.driver.api.driver.ChangeSetDependency;
import io.changock.driver.api.driver.ConnectionDriver;
import io.changock.driver.api.driver.ForbiddenParametersMap;
import io.changock.driver.api.entry.ChangeEntryService;
import io.changock.driver.api.lock.LockManager;
import io.changock.migration.api.exception.ChangockException;
import io.changock.runner.spring.v5.profiles.enseuredecorators.EnsureDecoratorChangerLog;
import io.changock.runner.spring.v5.profiles.integration.IntegrationProfiledChangerLog;
import io.changock.runner.spring.v5.profiles.withForbiddenParameter.ChangeLogWithForbiddenParameter;
import io.changock.runner.spring.v5.profiles.withForbiddenParameter.ForbiddenParameter;
import io.changock.runner.spring.v5.util.CallVerifier;
import io.changock.runner.spring.v5.util.MongockTemplateForTest;
import io.changock.runner.spring.v5.util.MongockTemplateForTestImpl;
import io.changock.runner.spring.v5.util.MongockTemplateForTestImplChild;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.internal.verification.Times;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SpringChangockInitializingBeanRunnerTest {

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
    when(springContext.getBean(MongockTemplateForTest.class))
        .thenReturn(new MongockTemplateForTestImpl());
  }

  @Test
  public void shouldRunOnlyProfiledChangeSets() {

    // when
//        Spring5Runner.builder()
    ChangockSpring5.builder()
        .setDriver(driver)
        .addChangeLogsScanPackage(IntegrationProfiledChangerLog.class.getPackage().getName())
        .setSpringContext(springContext)
        .buildInitializingBeanRunner()
        .afterPropertiesSet();

    // then
    ArgumentCaptor<String> changeSetIdCaptor = ArgumentCaptor.forClass(String.class);
    int wantedNumberOfInvocations = 3 + 1; // 3 -> Number of changes, 1 -> Pre migration check
    verify(changeEntryService, new Times(wantedNumberOfInvocations)).isAlreadyExecuted(changeSetIdCaptor.capture(), anyString());

    List<String> changeSetIdList = changeSetIdCaptor.getAllValues();
    assertEquals(wantedNumberOfInvocations, changeSetIdList.size());
    assertEquals(2, Collections.frequency(changeSetIdList, "testWithProfileIncluded1"));
    assertTrue(changeSetIdList.contains("testWithProfileIncluded2"));
    assertTrue(changeSetIdList.contains("testWithProfileIncluded1OrProfileINotIncluded"));
  }

  @Test
  public void shouldInjectEnvironmentToChangeSet() {
    // given
    when(changeEntryService.isAlreadyExecuted("testWithProfileIncluded1OrProfileINotIncluded", "testuser"))
        .thenReturn(false);

    // when
    ChangockSpring5.builder()
        .setDriver(driver)
        .addChangeLogsScanPackage(IntegrationProfiledChangerLog.class.getPackage().getName())
        .setSpringContext(springContext)
        .buildInitializingBeanRunner()
        .afterPropertiesSet();

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
    ChangockSpring5.builder()
        .setDriver(driver)
        .addChangeLogsScanPackage(EnsureDecoratorChangerLog.class.getPackage().getName())
        .setSpringContext(springContext)
        .buildInitializingBeanRunner()
        .afterPropertiesSet();

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
        .buildInitializingBeanRunner()
        .afterPropertiesSet();
  }

  @Test
  public void shouldFail_whenRunningChangeSet_ifForbiddenParameterFromDriver() {

    when(changeEntryService.isAlreadyExecuted("withForbiddenParameter", "executor")).thenReturn(true);

    // then
    exceptionExpected.expect(ChangockException.class);
    exceptionExpected.expectMessage("Error in method[ChangeLogWithForbiddenParameter.withForbiddenParameter] : Forbidden parameter[ForbiddenParameter]. Must be replaced with [String]");

    // when
    ChangockSpring5.builder()
        .setDriver(driver)
        .addChangeLogsScanPackage(ChangeLogWithForbiddenParameter.class.getPackage().getName())
        .setSpringContext(springContext)
        .buildApplicationRunner()
        .run(null);
  }

}
