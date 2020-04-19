package io.changock.runner.spring.v5;

import io.changock.driver.api.driver.ConnectionDriver;
import io.changock.migration.api.exception.ChangockException;
import io.changock.runner.core.MigrationExecutor;
import io.changock.runner.spring.util.SpringDependencyManager;


import java.util.Map;

/**
 * Child class from MigrationExecutor to force SpringDependencyManager
 */
public class SpringMigrationExecutor extends MigrationExecutor {
    public SpringMigrationExecutor(ConnectionDriver driver, SpringDependencyManager dependencyManager, long lockAcquiredForMinutes, int maxTries, long maxWaitingForLockMinutes, Map<String, Object> metadata) {
        super(driver, dependencyManager, lockAcquiredForMinutes, maxTries, maxWaitingForLockMinutes, metadata);
    }

    @Override
    public void initializationAndValidation() throws ChangockException {
        super.initializationAndValidation();
        ((SpringDependencyManager) this.dependencyManager).runValidation();
    }
}
