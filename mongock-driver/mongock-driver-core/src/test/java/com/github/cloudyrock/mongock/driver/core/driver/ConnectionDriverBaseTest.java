package com.github.cloudyrock.mongock.driver.core.driver;


import com.github.cloudyrock.mongock.driver.api.driver.ChangeSetDependency;
import com.github.cloudyrock.mongock.driver.api.driver.Transactioner;
import com.github.cloudyrock.mongock.driver.api.entry.ChangeEntry;
import com.github.cloudyrock.mongock.driver.api.entry.ChangeEntryService;
import com.github.cloudyrock.mongock.driver.api.lock.LockManager;
import com.github.cloudyrock.mongock.driver.core.lock.LockRepository;
import com.github.cloudyrock.mongock.exception.MongockException;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.verification.Times;

import java.util.Optional;
import java.util.Set;

public class ConnectionDriverBaseTest {


  @Test
  public void shouldInitializeRepositories() {
    // given
    LockRepository<ChangeEntry> lockRepository = Mockito.mock(LockRepository.class);
    ChangeEntryService<ChangeEntry> changeEntryService = Mockito.mock(ChangeEntryService.class);

    ConnectionDriverBase<ChangeEntry> driver = new ConnectionDriverBaseTestImpl(
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


  static class ConnectionDriverBaseTestImpl extends ConnectionDriverBase<ChangeEntry> {

    private final LockRepository<ChangeEntry> lockRepository;
    private final ChangeEntryService<ChangeEntry> changeEntryService;
    private final LockManager lockManager;


    private static long minutesToMillis(long minutes) {
      return minutes * 60 * 1000;
    }

    ConnectionDriverBaseTestImpl(long lockAcquiredForMinutes,
                                 long maxWaitingForLockMinutesEachTry,
                                 int maxTries,
                                 LockRepository<ChangeEntry> lockRepository,
                                 ChangeEntryService<ChangeEntry> changeEntryService,
                                 LockManager lockManager) {
      super(
          minutesToMillis(lockAcquiredForMinutes),
          minutesToMillis(maxWaitingForLockMinutesEachTry * maxTries),
          1000L
      );
      this.lockRepository = lockRepository;
      this.changeEntryService = changeEntryService;
      this.lockManager = lockManager;
    }

    @Override
    protected LockRepository<ChangeEntry> getLockRepository() {
      return lockRepository;
    }

    @Override
    protected void specificInitialization() {

    }

    @Override
    public ChangeEntryService<ChangeEntry> getChangeEntryService() {
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
    public Class<?> getLegacyMigrationChangeLogClass(boolean runAlways) {
      return null;
    }

    @Override
    public void runValidation() throws MongockException {

    }


    @Override
    public void disableTransaction() {
      //Empty method
    }

    @Override
    public Optional<Transactioner> getTransactioner() {
      return Optional.empty();
    }
  }


}
