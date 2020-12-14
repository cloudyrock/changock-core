package io.changock.migration.api.config;

import io.changock.migration.api.annotations.NonLockGuarded;
import io.changock.migration.api.annotations.NonLockGuardedType;

import java.util.Objects;

@NonLockGuarded(NonLockGuardedType.NONE)
public abstract class LegacyMigration {

  private String origin;

  private boolean failFast = true;

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

  public String getOrigin() {
    return origin;
  }

  public void setOrigin(String origin) {
    this.origin = origin;
  }

  public boolean isFailFast() {
    return failFast;
  }

  public void setFailFast(boolean failFast) {
    this.failFast = failFast;
  }
}
