package io.changock.migration.api.config;

import io.changock.migration.api.annotations.NonLockGuarded;
import io.changock.migration.api.annotations.NonLockGuardedType;

import java.util.Objects;

@NonLockGuarded(NonLockGuardedType.NONE)
public abstract class LegacyMigration {

  private Integer changesCountExpectation = null;

  private LegacyMigrationMappingFields mappingFields = new LegacyMigrationMappingFields();

  private boolean runAlways = false;

  public LegacyMigrationMappingFields getMappingFields() {
    return mappingFields;
  }

  public void setMappingFields(LegacyMigrationMappingFields mappingFields) {
    this.mappingFields = mappingFields;
  }

  public Integer getChangesCountExpectation() {
    return changesCountExpectation;
  }

  public void setChangesCountExpectation(Integer changesCountExpectation) {
    this.changesCountExpectation = changesCountExpectation;
  }

  public boolean isRunAlways() {
    return runAlways;
  }

  public void setRunAlways(boolean runAlways) {
    this.runAlways = runAlways;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    LegacyMigration that = (LegacyMigration) o;
    return Objects.equals(mappingFields, that.mappingFields);
  }

  @Override
  public int hashCode() {
    return Objects.hash(mappingFields);
  }
}
