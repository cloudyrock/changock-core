package com.github.cloudyrock.mongock.runner.core;


import com.github.cloudyrock.mongock.ChangeLogItem;
import com.github.cloudyrock.mongock.ChangeSetItem;
import com.github.cloudyrock.mongock.exception.MongockException;
import com.github.cloudyrock.mongock.runner.core.changelogs.comparator.Comparator1ChangeLog;
import com.github.cloudyrock.mongock.runner.core.changelogs.comparator.Comparator2ChangeLog;
import com.github.cloudyrock.mongock.runner.core.changelogs.instantiator.bad.BadChangeLogCustomConstructor;
import com.github.cloudyrock.mongock.runner.core.changelogs.instantiator.good.ChangeLogCustomConstructor;
import com.github.cloudyrock.mongock.runner.core.changelogs.multipackage.ChangeLogNoPackage;
import com.github.cloudyrock.mongock.runner.core.changelogs.multipackage.package1.ChangeLogMultiPackage1;
import com.github.cloudyrock.mongock.runner.core.changelogs.multipackage.package2.ChangeLogMultiPackage2;
import com.github.cloudyrock.mongock.runner.core.changelogs.systemversion.ChangeLogSystemVersion;
import com.github.cloudyrock.mongock.runner.core.changelogs.test1.ChangeLogSuccess11;
import com.github.cloudyrock.mongock.runner.core.changelogs.test1.ChangeLogSuccess12;
import com.github.cloudyrock.mongock.runner.core.changelogs.withnoannotations.ChangeLogNormal;
import com.github.cloudyrock.mongock.runner.core.executor.changelog.ChangeLogService;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ChangeLogServiceTest {

  @Test
  public void shouldSucceed_WhenValidate_ifParametersAreOk() {
    new ChangeLogService(Collections.singletonList("fake.changelog.package"), Collections.emptyList(), "0", "999")
        .runValidation();
  }

  @Test(expected = MongockException.class)
  public void shouldFail_WhenValidate_ifParametersEmpty() {
    new ChangeLogService(Collections.emptyList(), Collections.emptyList(), "0", "999")
        .runValidation();
  }

  private static Function<Class<?>, Object> mockInjector() {
    return (type) -> {
      try {
        if (type == ChangeLogCustomConstructor.class) {
          return type.getConstructor(String.class, int.class).newInstance("string", 10);
        } else if (type == BadChangeLogCustomConstructor.class) {
          throw new RuntimeException("Cannot instantiate BadChangeLogCustomConstructor");
        } else {
          return type.getConstructor().newInstance();
        }
      } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
        throw new MongockException(e);
      }
    };
  }

  @Test
  public void shouldUseCustomChangeLogInstantiator() {
    List<ChangeLogItem<ChangeSetItem>> changeLogItems = new ArrayList<>(new ChangeLogService(
        Collections.singletonList(ChangeLogCustomConstructor.class.getPackage().getName()),
        Collections.emptyList(),
        "0",
        "9999",
        null,
        mockInjector()
    ).fetchChangeLogs());

    assertEquals(1, changeLogItems.size());
    assertEquals(ChangeLogCustomConstructor.class, changeLogItems.get(0).getInstance().getClass());
    ChangeLogCustomConstructor changeLogCustomConstructor = (ChangeLogCustomConstructor) changeLogItems.get(0).getInstance();
    assertEquals("string", changeLogCustomConstructor.getStringValue());
    assertEquals(10, changeLogCustomConstructor.getIntegerValue());
  }

  /**
   * The MockInjector will fail to instantiate BadChangeLogCustomConstructor by throwing an Exception.
   * This exception should be repackaged as a MongockException
   */
  @Test(expected = MongockException.class)
  public void shouldHandleExceptionsFromCustomChangeLogInstantiator() {
    new ChangeLogService(
        Collections.singletonList(BadChangeLogCustomConstructor.class.getPackage().getName()),
        Collections.emptyList(),
        "0",
        "9999",
        null,
        mockInjector()
    ).fetchChangeLogs();
  }

  @Test
  public void shouldOnlyRunAnnotatedClassesAndMethods() {
    List<ChangeLogItem<ChangeSetItem>> changeLogItemList = new ArrayList<>(new ChangeLogService(
        Collections.singletonList(ChangeLogNormal.class.getPackage().getName()),
        Collections.emptyList(),
        "0",
        "9999"
    ).fetchChangeLogs());

    assertEquals(3, changeLogItemList.size());

    // Normal
    ChangeLogItem<ChangeSetItem> changeLogItem = changeLogItemList.get(0);
    assertEquals(1, changeLogItem.getChangeSetElements().size());
    assertEquals("changeSet_0", changeLogItem.getChangeSetElements().get(0).getId());

    // With both, one annotated changeSet and a method with no annotation
    changeLogItem = changeLogItemList.get(1);
    assertEquals(1, changeLogItem.getChangeSetElements().size());
    assertEquals("changeSet_1", changeLogItem.getChangeSetElements().get(0).getId());

    // ChangeLog annotated class, with no annotated changeSet
    changeLogItem = changeLogItemList.get(2);
    assertEquals(0, changeLogItem.getChangeSetElements().size());
  }

  @Test
  public void shouldReturnRightChangeLogItems_whenFetchingLogs_ifPackageIsRight() {
    List<ChangeLogItem<ChangeSetItem>> changeLogItemList = new ArrayList<>(new ChangeLogService(
        Collections.singletonList(ChangeLogSuccess11.class.getPackage().getName()),
        Collections.emptyList(),
        "0",
        "9999"
    ).fetchChangeLogs());

    assertEquals(2, changeLogItemList.size());
    ChangeLogItem<ChangeSetItem> changeLogItem11 = changeLogItemList.get(0);
    validateChangeLog(changeLogItem11, 1);
    assertEquals(1, changeLogItem11.getChangeSetElements().size());
    changeLogItem11.getChangeSetElements().forEach(changeSetItem -> validateChangeSet(changeSetItem, 1));

    ChangeLogItem<ChangeSetItem> changeLogItem12 = changeLogItemList.get(1);
    validateChangeLog(changeLogItem12, 2);
    assertEquals(1, changeLogItem12.getChangeSetElements().size());
    changeLogItem12.getChangeSetElements().forEach(changeSetItem -> validateChangeSet(changeSetItem, 2));
  }

  @Test
  public void shouldReturnOnlyChangeSetsWithinSystemVersionRangeInclusive() {
    List<? extends ChangeSetItem> allChangeSets = getChangeSetItems("0", "9");
    assertEquals(6, allChangeSets.size());

    List<? extends ChangeSetItem> systemVersionedChangeSets = getChangeSetItems("2", "4");
    assertEquals(3, systemVersionedChangeSets.size());
    systemVersionedChangeSets.stream()
        .map(ChangeSetItem::getId)
        .collect(Collectors.toList())
        .containsAll(Arrays.asList("ChangeSet_2", "ChangeSet_3.0", "ChangeSet_4"));

    systemVersionedChangeSets = getChangeSetItems("3", "4");
    assertEquals(2, systemVersionedChangeSets.size());
    systemVersionedChangeSets.stream()
        .map(ChangeSetItem::getId)
        .collect(Collectors.toList())
        .containsAll(Arrays.asList("ChangeSet_3.0", "ChangeSet_4"));

    systemVersionedChangeSets = getChangeSetItems("3", "2018");
    assertEquals(5, systemVersionedChangeSets.size());
    systemVersionedChangeSets.stream()
        .map(ChangeSetItem::getId)
        .collect(Collectors.toList())
        .containsAll(Arrays.asList("ChangeSet_3.0", "ChangeSet_4", "ChangeSet_5", "ChangeSet_6", "ChangeSet_2018"));
  }

  private List<? extends ChangeSetItem> getChangeSetItems(String startingVersion, String endingVersion) {
    return new ArrayList<>(new ChangeLogService(
        Collections.singletonList(ChangeLogSystemVersion.class.getPackage().getName()),
        Collections.emptyList(),
        startingVersion,
        endingVersion)
        .fetchChangeLogs())
        .get(0)
        .getChangeSetElements();
  }


  @Test
  public void shouldReturnChangeSetsFromMultiplePackagesAndKeepsOrder() {
    List<ChangeLogItem<ChangeSetItem>> changeLogItemList = new ArrayList<>(new ChangeLogService(
        Arrays.asList(ChangeLogMultiPackage1.class.getPackage().getName(), ChangeLogMultiPackage2.class.getPackage().getName()),
        Collections.emptyList(),
        "0",
        "9999"
    ).fetchChangeLogs());

    assertEquals(2, changeLogItemList.size());
    ChangeLogItem<ChangeSetItem> changeLogPackage = changeLogItemList.get(0);
    assertEquals(1, changeLogPackage.getChangeSetElements().size());
    ChangeSetItem changeSet = changeLogPackage.getChangeSetElements().get(0);
    assertEquals("changeset_package1", changeSet.getId());
    assertEquals("changeSetPackage1", changeSet.getMethod().getName());


    changeLogPackage = changeLogItemList.get(1);
    assertEquals(1, changeLogPackage.getChangeSetElements().size());
    changeSet = changeLogPackage.getChangeSetElements().get(0);
    assertEquals("changeset_package2", changeSet.getId());
    assertEquals("changeSetPackage2", changeSet.getMethod().getName());

  }


  @Test
  public void shouldReturnChangeSetsFromMultiplePackagesAndIsolatedClassesAndKeepsOrder() {
    List<String> changeLogsBasePackageList = Arrays.asList(
        ChangeLogMultiPackage1.class.getPackage().getName(),
        ChangeLogMultiPackage2.class.getPackage().getName(),
        ChangeLogNoPackage.class.getName());
    List<ChangeLogItem<ChangeSetItem>> changeLogItemList = new ArrayList<>(new ChangeLogService(
        changeLogsBasePackageList,
        Collections.emptyList(),
        "0",
        "9999"
    ).fetchChangeLogs());

    //package 1
    assertEquals(3, changeLogItemList.size());
    ChangeLogItem<ChangeSetItem> changeLogPackage = changeLogItemList.get(0);
    assertEquals(1, changeLogPackage.getChangeSetElements().size());
    ChangeSetItem changeSet1 = changeLogPackage.getChangeSetElements().get(0);
    assertEquals("changeset_package1", changeSet1.getId());
    assertEquals("changeSetPackage1", changeSet1.getMethod().getName());

    //isolated class
    changeLogPackage = changeLogItemList.get(1);
    assertEquals(2, changeLogPackage.getChangeSetElements().size());
    ChangeSetItem changeSet2 = changeLogPackage.getChangeSetElements().get(0);
    assertEquals("no_package", changeSet2.getId());
    assertEquals("noPackage", changeSet2.getMethod().getName());

    ChangeSetItem changeSet3 = changeLogPackage.getChangeSetElements().get(1);
    assertEquals("no_package_2", changeSet3.getId());
    assertEquals("noPackage2", changeSet3.getMethod().getName());

    //package 2
    changeLogPackage = changeLogItemList.get(2);
    assertEquals(1, changeLogPackage.getChangeSetElements().size());
    ChangeSetItem changeSet4 = changeLogPackage.getChangeSetElements().get(0);
    assertEquals("changeset_package2", changeSet4.getId());
    assertEquals("changeSetPackage2", changeSet4.getMethod().getName());

  }

  private void validateChangeLog(ChangeLogItem<ChangeSetItem> changeLogItem, int number) {
    assertEquals(String.valueOf(number), changeLogItem.getOrder());
  }

  private void validateChangeSet(ChangeSetItem changeSetItem, int number) {
    assertEquals("testUser1" + number, changeSetItem.getAuthor());
    assertEquals("ChangeSet_12" + number, changeSetItem.getId());
    assertEquals(String.valueOf(number), changeSetItem.getOrder());
    assertTrue(changeSetItem.isRunAlways());
    assertEquals(String.valueOf(number), changeSetItem.getSystemVersion());
  }


  @Test
  public void shouldReturnChangelogs() {
    List<ChangeLogItem<ChangeSetItem>> changeLogItemList = new ArrayList<>(new ChangeLogService(
        Arrays.asList(Comparator1ChangeLog.class.getPackage().getName()),
        Collections.emptyList(),
        "0",
        "9999"
    ).fetchChangeLogs());

    assertEquals(2, changeLogItemList.size());
    changeLogItemList.forEach(changeLogItem -> assertTrue(changeLogItem.getType() == Comparator1ChangeLog.class
            || changeLogItem.getType() == Comparator2ChangeLog.class));

  }


  @Test
  public void shouldNotDuplicateWhenAddingSingleClassIfTwice() {

    ChangeLogService changeLogService = new ChangeLogService();
    changeLogService.setChangeLogsBaseClassList(Arrays.asList(ChangeLogSuccess11.class, ChangeLogSuccess11.class));

    List<ChangeLogItem<ChangeSetItem>> changeLogs = new ArrayList<>(changeLogService.fetchChangeLogs());

    assertEquals(1, changeLogs.size());

  }


  @Test
  public void shouldAddClassAndPackage() {


    ChangeLogService changeLogService = new ChangeLogService();
    changeLogService.setChangeLogsBaseClassList(Collections.singletonList(ChangeLogSuccess11.class));
    changeLogService.setChangeLogsBasePackageList(Collections.singletonList(ChangeLogSuccess11.class.getPackage().getName()));

    List<ChangeLogItem<ChangeSetItem>> changeLogItemsList = new ArrayList<>(changeLogService.fetchChangeLogs());

    assertEquals(2, changeLogItemsList.size());

    ChangeLogItem<ChangeSetItem> changeLogItem = changeLogItemsList.get(0);
    assertEquals(ChangeLogSuccess11.class, changeLogItem.getType());
    assertEquals("1", changeLogItem.getOrder());

    ChangeLogItem<ChangeSetItem> changeLogItem2 = changeLogItemsList.get(1);
    assertEquals(ChangeLogSuccess12.class, changeLogItem2.getType());
    assertEquals("2", changeLogItem2.getOrder());
  }

  @Test
  public void shouldAddSingleClass() {


    ChangeLogService changeLogService = new ChangeLogService();
    changeLogService.setChangeLogsBaseClassList(Collections.singletonList(ChangeLogSuccess11.class));

    List<ChangeLogItem<ChangeSetItem>> changeLogs = new ArrayList<>(changeLogService.fetchChangeLogs());


    ChangeLogItem<ChangeSetItem> changeLogItem = changeLogs.get(0);
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

}
