package com.github.cloudyrock.mongock.runner.core.builder.roles;

import com.github.cloudyrock.mongock.config.LegacyMigration;

public interface LegacyMigrator<BUILDER_TYPE extends LegacyMigrator> {
  /**
   * Adds a legacy migration to be executed before the actual migration
   *
   * @param legacyMigration represents the legacy migration
   * @return builder for fluent interface
   */
  BUILDER_TYPE setLegacyMigration(LegacyMigration legacyMigration);
}
