package io.changock.driver.api.entry;


import io.changock.migration.api.ChangeSetItem;
import io.changock.utils.field.FieldInstance;
import io.changock.utils.field.FieldUtil;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChangeEntryTest {


  @Test
  public void fields() throws NoSuchMethodException {
    // given
    Map<String, Object> fieldExpectation = getExpectation();
    ChangeEntry entry = getChangeEntry();

    // when
    List<FieldInstance> instances = FieldUtil.getAllFields(entry.getClass())
        .stream()
        .map(field -> new FieldInstance(field, entry))
        .collect(Collectors.toList());
    // then
    Assert.assertEquals(fieldExpectation.size(), instances.size());
    Assert.assertTrue(instances.stream()
        .allMatch(field ->
            "timestamp".equals(field.getName()) || fieldExpectation.get(field.getName()).equals(field.getValue())));
  }

  private ChangeEntry getChangeEntry() throws NoSuchMethodException {
    ChangeSetItem changeSetItem = new ChangeSetItem(
        "changeId",
        "changeAuthor",
        "changeOrder",
        true,
        "systemVersion",
        true,
        ChangeEntryTest.class.getMethod("changeSetMethod")
    );

    Map<String, String> metadata = new HashMap<>();
    metadata.put("field", "value");
    return ChangeEntry.createInstance(
        "migrationExecutionId",
        ChangeState.EXECUTED,
        changeSetItem,
        333,
        metadata
    );
  }

  private Map<String, Object> getExpectation() {
    Map<String, String> metadata = new HashMap<>();
    metadata.put("field", "value");
    Map<String, Object> fieldExpectation = new HashMap<>();
    fieldExpectation.put("executionId", "migrationExecutionId");
    fieldExpectation.put("changeId", "changeId");
    fieldExpectation.put("author", "changeAuthor");
    fieldExpectation.put("timestamp", null);
    fieldExpectation.put("state", "EXECUTED");
    fieldExpectation.put("changeLogClass", ChangeEntryTest.class.getName());
    fieldExpectation.put("changeSetMethod", "changeSetMethod");
    fieldExpectation.put("metadata", metadata);
    fieldExpectation.put("executionMillis", 333L);
    return fieldExpectation;
  }

  public void changeSetMethod() {
  }

}
