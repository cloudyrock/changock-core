package com.github.cloudyrock.mongock.driver.api.driver;

public interface DriverLegaciable {
  Class getLegacyMigrationChangeLogClass(boolean runAlways);
}
