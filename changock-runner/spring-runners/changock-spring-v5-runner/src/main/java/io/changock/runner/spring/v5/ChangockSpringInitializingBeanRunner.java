package io.changock.runner.spring.v5;

import org.springframework.beans.factory.InitializingBean;

public class ChangockSpringInitializingBeanRunner extends ChangockSpring5Runner implements InitializingBean {

    ChangockSpringInitializingBeanRunner(SpringMigrationExecutor executor, ProfiledChangeLogService changeLogService, boolean throwExceptionIfCannotObtainLock, boolean enabled) {
        super(executor, changeLogService, throwExceptionIfCannotObtainLock, enabled);
    }

    @Override
    public void afterPropertiesSet() {
        execute();
    }
}
