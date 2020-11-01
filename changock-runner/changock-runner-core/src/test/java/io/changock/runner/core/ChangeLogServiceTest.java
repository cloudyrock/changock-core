package io.changock.runner.core;


import io.changock.migration.api.ChangeLogItem;
import io.changock.migration.api.ChangeSetItem;
import io.changock.migration.api.exception.ChangockException;
import io.changock.runner.core.changelogs.comparator.Comparator1ChangeLog;
import io.changock.runner.core.changelogs.comparator.Comparator2ChangeLog;
import io.changock.runner.core.changelogs.multipackage.ChangeLogNoPackage;
import io.changock.runner.core.changelogs.multipackage.package1.ChangeLogMultiPackage1;
import io.changock.runner.core.changelogs.multipackage.package2.ChangeLogMultiPackage2;
import io.changock.runner.core.changelogs.systemversion.ChangeLogSystemVersion;
import io.changock.runner.core.changelogs.test1.ChangeLogSuccess11;
import io.changock.runner.core.changelogs.withnoannotations.ChangeLogNormal;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ChangeLogServiceTest {

  @Test
  public void shouldSucceed_WhenValidate_ifParametersAreOk() {
    new ChangeLogService(Collections.singletonList("fake.changelog.package"), Collections.emptyList(), "0", "999")
        .runValidation();
  }

  @Test(expected = ChangockException.class)
  public void shouldFail_WhenValidate_ifParametersEmpty() {
    new ChangeLogService(Collections.emptyList(), Collections.emptyList(), "0", "999")
        .runValidation();
  }

  @Test
  public void shouldOnlyRunAnnotatedClassesAndMethods() {
    List<ChangeLogItem> changeLogItemList = new ArrayList<>(new ChangeLogService(
        Collections.singletonList(ChangeLogNormal.class.getPackage().getName()),
        Collections.emptyList(),
        "0",
        "9999"
    ).fetchChangeLogs());

    assertEquals(3, changeLogItemList.size());

    // Normal
    ChangeLogItem changeLogItem = changeLogItemList.get(0);
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
    List<ChangeLogItem> changeLogItemList = new ArrayList<>(new ChangeLogService(
        Collections.singletonList(ChangeLogSuccess11.class.getPackage().getName()),
        Collections.emptyList(),
        "0",
        "9999"
    ).fetchChangeLogs());

    assertEquals(2, changeLogItemList.size());
    ChangeLogItem changeLogItem11 = changeLogItemList.get(0);
    validateChangeLog(changeLogItem11, 1);
    assertEquals(1, changeLogItem11.getChangeSetElements().size());
    changeLogItem11.getChangeSetElements().forEach(changeSetItem -> validateChangeSet(changeSetItem, 1));

    ChangeLogItem changeLogItem12 = changeLogItemList.get(1);
    validateChangeLog(changeLogItem12, 2);
    assertEquals(1, changeLogItem12.getChangeSetElements().size());
    changeLogItem12.getChangeSetElements().forEach(changeSetItem -> validateChangeSet(changeSetItem, 2));
  }

  @Test
  public void shouldReturnOnlyChangeSetsWithinSystemVersionRangeInclusive() {
    List<ChangeSetItem> allChangeSets = getChangeSetItems("0", "9");
    assertEquals(6, allChangeSets.size());

    List<ChangeSetItem> systemVersionedChangeSets = getChangeSetItems("2", "4");
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

  private List<ChangeSetItem> getChangeSetItems(String startingVersion, String endingVersion) {
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
    List<ChangeLogItem> changeLogItemList = new ArrayList<>(new ChangeLogService(
        Arrays.asList(ChangeLogMultiPackage1.class.getPackage().getName(), ChangeLogMultiPackage2.class.getPackage().getName()),
        Collections.emptyList(),
        "0",
        "9999"
    ).fetchChangeLogs());

    assertEquals(2, changeLogItemList.size());
    ChangeLogItem changeLogPackage = changeLogItemList.get(0);
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
    List<ChangeLogItem> changeLogItemList = new ArrayList<>(new ChangeLogService(
        changeLogsBasePackageList,
        Collections.emptyList(),
        "0",
        "9999"
    ).fetchChangeLogs());

    //package 1
    assertEquals(3, changeLogItemList.size());
    ChangeLogItem changeLogPackage = changeLogItemList.get(0);
    assertEquals(1, changeLogPackage.getChangeSetElements().size());
    ChangeSetItem changeSet = changeLogPackage.getChangeSetElements().get(0);
    assertEquals("changeset_package1", changeSet.getId());
    assertEquals("changeSetPackage1", changeSet.getMethod().getName());

    //isolated class
    changeLogPackage = changeLogItemList.get(1);
    assertEquals(2, changeLogPackage.getChangeSetElements().size());
    changeSet = changeLogPackage.getChangeSetElements().get(0);
    assertEquals("no_package", changeSet.getId());
    assertEquals("noPackage", changeSet.getMethod().getName());
    changeSet = changeLogPackage.getChangeSetElements().get(1);
    assertEquals("no_package_2", changeSet.getId());
    assertEquals("noPackage2", changeSet.getMethod().getName());

    //package 2
    changeLogPackage = changeLogItemList.get(2);
    assertEquals(1, changeLogPackage.getChangeSetElements().size());
    changeSet = changeLogPackage.getChangeSetElements().get(0);
    assertEquals("changeset_package2", changeSet.getId());
    assertEquals("changeSetPackage2", changeSet.getMethod().getName());

  }

  private void validateChangeLog(ChangeLogItem changeLogItem, int number) {
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
  public void shouldReturnC() {
    List<ChangeLogItem> changeLogItemList = new ArrayList<>(new ChangeLogService(
        Arrays.asList(Comparator1ChangeLog.class.getPackage().getName()),
        Collections.emptyList(),
        "0",
        "9999"
    ).fetchChangeLogs());

    assertEquals(2, changeLogItemList.size());
    changeLogItemList.forEach(changeLogItem -> assertTrue(changeLogItem.getType() == Comparator1ChangeLog.class
            || changeLogItem.getType() == Comparator2ChangeLog.class));

  }


}
