package com.github.cloudyrock.springboot.v2_2.base;


import com.github.cloudyrock.mongock.exception.MongockException;
import com.github.cloudyrock.mongock.driver.api.driver.ChangeSetDependency;
import com.github.cloudyrock.mongock.driver.api.driver.ConnectionDriver;
import com.github.cloudyrock.mongock.driver.api.entry.ChangeEntryService;
import com.github.cloudyrock.mongock.driver.api.lock.LockManager;
import com.github.cloudyrock.springboot.v2_2.base.util.TemplateForTest;
import com.github.cloudyrock.springboot.v2_2.base.util.TemplateForTestImplChild;
import com.github.cloudyrock.springboot.v2_2.base.util.CallVerifier;
import com.github.cloudyrock.springboot.v2_2.base.util.TemplateForTestImpl;
import com.github.cloudyrock.springboot.v2_2.base.profiles.enseuredecorators.EnsureDecoratorChangerLog;
import com.github.cloudyrock.springboot.v2_2.base.profiles.integration.IntegrationProfiledChangerLog;
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

public class SpringMongockInitializingBeanRunnerTest {

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

    callVerifier = new CallVerifier();
    Set<ChangeSetDependency> dependencySet = new HashSet<>();
    dependencySet.add(new ChangeSetDependency(CallVerifier.class, callVerifier));
    when(driver.getDependencies()).thenReturn(dependencySet);

    Environment environment = mock(Environment.class);
    when(environment.getActiveProfiles()).thenReturn(new String[]{"profileIncluded1", "profileIncluded2"});
    springContext = mock(ApplicationContext.class);
    when(springContext.getEnvironment()).thenReturn(environment);
    when(springContext.getBean(Environment.class)).thenReturn(environment);
    when(springContext.getBean(TemplateForTest.class))
        .thenReturn(new TemplateForTestImpl());
  }

  @Test
  public void shouldRunOnlyProfiledChangeSets() throws Exception {

    // when
//        Spring5Runner.builder()
    MongockSpringbootV2_2.builder()
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
  public void shouldInjectEnvironmentToChangeSet() throws Exception {
    // given
    when(changeEntryService.isAlreadyExecuted("testWithProfileIncluded1OrProfileINotIncluded", "testuser"))
        .thenReturn(false);

    // when
    MongockSpringbootV2_2.builder()
        .setDriver(driver)
        .addChangeLogsScanPackage(IntegrationProfiledChangerLog.class.getPackage().getName())
        .setSpringContext(springContext)
        .buildInitializingBeanRunner()
        .afterPropertiesSet();

    // then
    assertEquals(1, callVerifier.counter);
  }

  @Test
  public void shouldPrioritizeConnectorDependenciesOverContext() throws Exception {
    // given
    when(changeEntryService.isAlreadyExecuted("ensureDecoratorChangeSet", "testuser")).thenReturn(false);
    callVerifier = new CallVerifier();
    Set<ChangeSetDependency> dependencySet = new HashSet<>();
    dependencySet.add(new ChangeSetDependency(CallVerifier.class, callVerifier));
    dependencySet.add(new ChangeSetDependency(TemplateForTestImpl.class, new TemplateForTestImplChild()));
    when(driver.getDependencies()).thenReturn(dependencySet);

    Environment environment = mock(Environment.class);
    springContext = mock(ApplicationContext.class);
    when(springContext.getEnvironment()).thenReturn(environment);
    when(springContext.getBean(Environment.class)).thenReturn(environment);
    when(springContext.getBean(TemplateForTestImpl.class)).thenReturn(new TemplateForTestImpl());

    // when
    MongockSpringbootV2_2.builder()
        .setDriver(driver)
        .addChangeLogsScanPackage(EnsureDecoratorChangerLog.class.getPackage().getName())
        .setSpringContext(springContext)
        .buildInitializingBeanRunner()
        .afterPropertiesSet();

    // then
    assertEquals(1, callVerifier.counter);
  }

  @Test
  public void shouldFail_IfSpringContextNotInjected() throws Exception {

    exceptionExpected.expect(MongockException.class);
    exceptionExpected.expectMessage("ApplicationContext from Spring must be injected to Builder");

    MongockSpringbootV2_2.builder()
        .setDriver(driver)
        .addChangeLogsScanPackage(IntegrationProfiledChangerLog.class.getPackage().getName())
        .buildInitializingBeanRunner()
        .afterPropertiesSet();
  }

}
