package com.github.cloudyrock.mongock.driver.core.driver;


import com.github.cloudyrock.mongock.exception.MongockException;
import com.github.cloudyrock.mongock.driver.api.driver.ChangeSetDependency;
import com.github.cloudyrock.mongock.driver.api.driver.ForbiddenParametersMap;
import com.github.cloudyrock.mongock.driver.api.entry.ChangeEntryService;
import com.github.cloudyrock.mongock.driver.api.lock.LockManager;
import com.github.cloudyrock.mongock.driver.core.lock.LockRepository;
import com.github.cloudyrock.mongock.utils.TimeService;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.verification.Times;

import java.util.Set;

public class ConnectionDriverBaseTest {


  @Test
  public void shouldInitializeRepositories() {
    // given
    LockRepository lockRepository = Mockito.mock(LockRepository.class);
    ChangeEntryService changeEntryService = Mockito.mock(ChangeEntryService.class);

    ConnectionDriverBase driver = new ConnectionDriverBaseTestImpl(
        4,
        3,
        3,
        lockRepository,
        changeEntryService,
        Mockito.mock(LockManager.class));

    // when
    driver.initialize();

    // then
    Mockito.verify(lockRepository, new Times(1)).initialize();
    Mockito.verify(changeEntryService, new Times(1)).initialize();


  }


  static class ConnectionDriverBaseTestImpl extends ConnectionDriverBase {

    private static final TimeService TIME_SERVICE = new TimeService();

    private final LockRepository lockRepository;
    private final ChangeEntryService changeEntryService;
    private final LockManager lockManager;


    ConnectionDriverBaseTestImpl(long lockAcquiredForMinutes,
                                 long maxWaitingForLockMinutesEachTry,
                                 int maxTries,
                                 LockRepository lockRepository,
                                 ChangeEntryService changeEntryService,
                                 LockManager lockManager) {
      super(
          TIME_SERVICE.minutesLongToSecondsInt(lockAcquiredForMinutes),
          TIME_SERVICE.minutesLongToSecondsInt(maxWaitingForLockMinutesEachTry * maxTries),
          1
      );
      this.lockRepository = lockRepository;
      this.changeEntryService = changeEntryService;
      this.lockManager = lockManager;
    }

    @Override
    protected LockRepository getLockRepository() {
      return lockRepository;
    }

    @Override
    protected void specificInitialization() {

    }

    @Override
    public ChangeEntryService getChangeEntryService() {
      return changeEntryService;
    }

    @Override
    public LockManager getLockManager() {
      return lockManager;
    }

    @Override
    public Set<ChangeSetDependency> getDependencies() {
      return null;
    }

    @Override
    public ForbiddenParametersMap getForbiddenParameters() {
      return null;
    }

    @Override
    public Class getLegacyMigrationChangeLogClass(boolean runAlways) {
      return null;
    }

    @Override
    public void runValidation() throws MongockException {

    }
  }



}
