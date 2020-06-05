package io.changock.runner.core.builder.configuration;

import java.util.Objects;

public abstract class LegacyMigration {

  private LegacyMigrationMappingFields mappingFields;

  public LegacyMigrationMappingFields getMappingFields() {
    return mappingFields;
  }

  public void setMappingFields(LegacyMigrationMappingFields mappingFields) {
    this.mappingFields = mappingFields;
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
