package com.github.cloudyrock.mongock.runner.core.builder.roles;

import com.github.cloudyrock.mongock.config.LegacyMigration;

public interface LegacyMigrator<SELF extends LegacyMigrator<SELF>> {
  /**
   * Adds a legacy migration to be executed before the actual migration
   *
   * @param legacyMigration represents the legacy migration
   * @return builder for fluent interface
   */
  SELF setLegacyMigration(LegacyMigration legacyMigration);
}
