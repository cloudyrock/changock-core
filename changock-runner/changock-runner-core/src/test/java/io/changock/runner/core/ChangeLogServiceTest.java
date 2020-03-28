package io.changock.runner.core;


import io.changock.migration.api.ChangeLogItem;
import io.changock.migration.api.ChangeSetItem;
import io.changock.migration.api.exception.ChangockException;
import io.changock.runner.core.changelogs.systemversion.ChangeLogSystemVersion;
import io.changock.runner.core.changelogs.test1.ChangeLogSuccess11;
import io.changock.runner.core.changelogs.withnoannotations.ChangeLogNormal;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ChangeLogServiceTest {

  @Test
  public void shouldSucceed_WhenValidate_ifParametersAreOk() {
    new ChangeLogService(Collections.singletonList("fake.changelog.package"), "0", "999")
        .runValidation();
  }

  @Test(expected = ChangockException.class)
  public void shouldFail_WhenValidate_ifParametersEmpty() {
    new ChangeLogService(Collections.emptyList(), "0", "999")
        .runValidation();
  }

  @Test
  public void shouldOnlyRunAnnotatedClassesAndMethods() {
    List<ChangeLogItem> changeLogItemList = new ChangeLogService(
        Collections.singletonList(ChangeLogNormal.class.getPackage().getName()),
        "0",
        "9999"
    ).fetchChangeLogs();

    Assert.assertEquals(3, changeLogItemList.size());

    // Normal
    ChangeLogItem changeLogItem = changeLogItemList.get(0);
    Assert.assertEquals(1, changeLogItem.getChangeSetElements().size());
    Assert.assertEquals("changeSet_0", changeLogItem.getChangeSetElements().get(0).getId());

    // With both, one annotated changeSet and a method with no annotation
    changeLogItem = changeLogItemList.get(1);
    Assert.assertEquals(1, changeLogItem.getChangeSetElements().size());
    Assert.assertEquals("changeSet_1", changeLogItem.getChangeSetElements().get(0).getId());

    // ChangeLog annotated class, with no annotated changeSet
    changeLogItem = changeLogItemList.get(2);
    Assert.assertEquals(0, changeLogItem.getChangeSetElements().size());
  }

  @Test
  public void shouldReturnRightChangeLogItems_whenFetchingLogs_ifPackageIsRight() {
    List<ChangeLogItem> changeLogItemList = new ChangeLogService(
        Collections.singletonList(ChangeLogSuccess11.class.getPackage().getName()),
        "0",
        "9999"
    ).fetchChangeLogs();

    Assert.assertEquals(2, changeLogItemList.size());
    ChangeLogItem changeLogItem11 = changeLogItemList.get(0);
    validateChangeLog(changeLogItem11, 1);
    Assert.assertEquals(1, changeLogItem11.getChangeSetElements().size());
    changeLogItem11.getChangeSetElements().forEach(changeSetItem -> validateChangeSet(changeSetItem, 1));

    ChangeLogItem changeLogItem12 = changeLogItemList.get(1);
    validateChangeLog(changeLogItem12, 2);
    Assert.assertEquals(1, changeLogItem12.getChangeSetElements().size());
    changeLogItem12.getChangeSetElements().forEach(changeSetItem -> validateChangeSet(changeSetItem, 2));
  }

  @Test
  public void shouldReturnOnlyChangeSetsWithinSystemVersionRangeInclusive() {
    List<ChangeSetItem> allChangeSets = getChangeSetItems("0", "9");
    Assert.assertEquals(6, allChangeSets.size());

    List<ChangeSetItem> systemVersionedChangeSets = getChangeSetItems("2", "4");
    Assert.assertEquals(3, systemVersionedChangeSets.size());
    systemVersionedChangeSets.stream()
        .map(ChangeSetItem::getId)
        .collect(Collectors.toList())
        .containsAll(Arrays.asList("ChangeSet_2", "ChangeSet_3.0", "ChangeSet_4"));

    systemVersionedChangeSets = getChangeSetItems("3", "4");
    Assert.assertEquals(2, systemVersionedChangeSets.size());
    systemVersionedChangeSets.stream()
        .map(ChangeSetItem::getId)
        .collect(Collectors.toList())
        .containsAll(Arrays.asList("ChangeSet_3.0", "ChangeSet_4"));

    systemVersionedChangeSets = getChangeSetItems("3", "2018");
    Assert.assertEquals(5, systemVersionedChangeSets.size());
    systemVersionedChangeSets.stream()
        .map(ChangeSetItem::getId)
        .collect(Collectors.toList())
        .containsAll(Arrays.asList("ChangeSet_3.0", "ChangeSet_4", "ChangeSet_5", "ChangeSet_6", "ChangeSet_2018"));
  }

  private List<ChangeSetItem> getChangeSetItems(String startingVersion, String endingVersion) {
    return new ChangeLogService(
        Collections.singletonList(ChangeLogSystemVersion.class.getPackage().getName()),
        startingVersion,
        endingVersion)
        .fetchChangeLogs()
        .get(0)
        .getChangeSetElements();
  }


  private void validateChangeLog(ChangeLogItem changeLogItem, int number) {
    Assert.assertEquals(String.valueOf(number), changeLogItem.getOrder());
  }

  private void validateChangeSet(ChangeSetItem changeSetItem, int number) {
    Assert.assertEquals("testUser1" + number, changeSetItem.getAuthor());
    Assert.assertEquals("ChangeSet_12" + number, changeSetItem.getId());
    Assert.assertEquals(String.valueOf(number), changeSetItem.getOrder());
    Assert.assertTrue(changeSetItem.isRunAlways());
    Assert.assertEquals(String.valueOf(number), changeSetItem.getSystemVersion());
  }

}
