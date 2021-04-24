package com.github.cloudyrock.mongock.driver.core.lock;

import com.github.cloudyrock.mongock.driver.api.lock.LockCheckException;
import com.github.cloudyrock.mongock.driver.api.lock.LockManager;
import com.github.cloudyrock.mongock.utils.TimeService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.internal.verification.Times;

import java.time.Instant;
import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DefaultLockManagerTest {

  private static final long lockActiveMillis = 5 * 60 * 1000L;
  private static final long quitTryingAfterMillis = 3 * 60 * 1000L;
  private static final long tryFrequency = 5 * 1000L;
  public static final int DONT_CARE_LONG = 1;
  public static final Instant DONT_CARE_INSTANT = Instant.now();
  public static final Date FAR_DATE = new Date(100000L);

  private LockRepository lockRepository;
  private TimeService timeUtils;
  private LockManager lockManager;

  @Before
  public void setUp() {
    lockManager = new DefaultLockManager(lockRepository = Mockito.mock(LockRepository.class), timeUtils = Mockito.mock(TimeService.class))
        .setLockAcquiredForMillis(lockActiveMillis)
        .setLockQuitTryingAfterMillis(quitTryingAfterMillis)
        .setLockTryFrequencyMillis(tryFrequency);
  }

  @Test
  public void acquireLockShouldCallDaoFirstTime() throws LockPersistenceException, LockCheckException {
    //given
    Date expirationAt = new Date(1000L);
    when(timeUtils.currentDatePlusMillis(anyLong())).thenReturn(expirationAt);

    // when
    lockManager.acquireLockDefault();

    //then
    assertDaoInsertUpdateCalledWithRightParameters(expirationAt, 1);
  }

  @Test
  public void acquireLockShouldCallDaoSecondTimeWhenTimeHasAlreadyExpired() throws LockPersistenceException, LockCheckException {
    //given
    Date expirationAt = new Date(1000L);
    when(timeUtils.currentDatePlusMillis(anyLong())).thenReturn(expirationAt);
    when(timeUtils.currentTime()).thenReturn(new Date(40000L));// Exactly the expiration time(minus margin)
    lockManager.acquireLockDefault();

    //when
    lockManager.acquireLockDefault();

    //then
    assertDaoInsertUpdateCalledWithRightParameters(expirationAt, 2);
  }

  @Test
  public void acquireShouldCallDaoSecondTimeEvenWhenTimeHasNotExpiredYet() throws LockPersistenceException, LockCheckException {
    //given
    Date expirationAt = new Date(1000L);
    when(timeUtils.currentDatePlusMillis(anyLong())).thenReturn(expirationAt);
    when(timeUtils.currentTime()).thenReturn(new Date(39999L));// 1ms less than the expiration time(minus margin)
    lockManager.acquireLockDefault();

    // when
    lockManager.acquireLockDefault();

    //then
    assertDaoInsertUpdateCalledWithRightParameters(expirationAt, 2);
  }

  @Test
  public void acquireLockShouldWaitUntilExpirationTimeWhenDaoThrowsExceptionAndLockHeldByOther() throws LockPersistenceException, LockCheckException {
    //given
    long expiresAt = 3000L;
    long waitingTime = 0L;
    doThrow(new LockPersistenceException("acquireLockQuery", "newLockEntity", "dbErrorDetail"))
        .doNothing()
        .when(lockRepository).insertUpdate(any(LockEntry.class));

    when(lockRepository.findByKey(anyString())).thenReturn(createFakeLockWithOtherOwner(expiresAt));
    Date newExpirationAt = new Date(1000L);
    when(timeUtils.currentDatePlusMillis(anyLong())).thenReturn(newExpirationAt);
    when(timeUtils.currentTime())
        .thenReturn(new Date(expiresAt - waitingTime));

    //when
    long timeBeforeCall = System.currentTimeMillis();
    lockManager.acquireLockDefault();
    long timeSpent = System.currentTimeMillis() - timeBeforeCall;

    //then
    assertTrue("Checker should wait at least " + waitingTime + "ms", timeSpent >= waitingTime);
    assertDaoInsertUpdateCalledWithRightParameters(newExpirationAt, 2);
  }

  @Test
  public void acquireLockShouldNotWaitWhenWaitForLockIsFalse() throws LockPersistenceException, LockCheckException {
    //given
    lockManager.setLockQuitTryingAfterMillis(1000L);

    doThrow(new LockPersistenceException("acquireLockQuery", "newLockEntity", "dbErrorDetail"))
        .doNothing()
        .when(lockRepository).insertUpdate(any(LockEntry.class));

    when(lockRepository.findByKey(anyString())).thenReturn(createFakeLockWithOtherOwner(DONT_CARE_LONG));
    when(timeUtils.currentDatePlusMillis(anyLong())).thenReturn(FAR_DATE);
    when(timeUtils.currentTime()).thenReturn(new Date(2000L));
    when(timeUtils.nowPlusMillis(anyLong())).thenReturn(DONT_CARE_INSTANT);
    when(timeUtils.isPast(any(Instant.class))).thenReturn(true);


    //when
    long timeBeforeCall = System.currentTimeMillis();
    boolean exceptionThrown = false;
    try {
      lockManager.acquireLockDefault();
    } catch (LockCheckException ex) {
      exceptionThrown = true;
    }
    assertTrue(exceptionThrown);
    long timeSpent = System.currentTimeMillis() - timeBeforeCall;

    //then
    assertTrue("Checker should not wait at all waiting time", timeSpent <= quitTryingAfterMillis);
    assertDaoInsertUpdateCalledWithRightParameters(FAR_DATE, 1);
  }

  @Test
  public void acquireLockShouldNotWaitWhenDaoThrowsExceptionButLockHeldByTheSameOwner() throws LockPersistenceException, LockCheckException {
    //given
    long expiresAt = 3000L;
    long waitingTime = 1000L;
    doThrow(new LockPersistenceException("acquireLockQuery", "newLockEntity", "dbErrorDetail"))
        .doNothing()
        .when(lockRepository).insertUpdate(any(LockEntry.class));

    when(lockRepository.findByKey(anyString())).thenReturn(new LockEntry(
        lockManager.getDefaultKey(),
        LockStatus.LOCK_HELD.name(),
        lockManager.getOwner(),
        new Date(expiresAt)
    ));
    Date newExpirationAt = new Date(100000L);
    when(timeUtils.currentDatePlusMillis(anyLong())).thenReturn(newExpirationAt);
    when(timeUtils.currentTime())
        .thenReturn(new Date(40000L))
        .thenReturn(new Date(expiresAt - waitingTime));

    //when
    long timeBeforeCall = System.currentTimeMillis();
    lockManager.acquireLockDefault();
    long timeSpent = System.currentTimeMillis() - timeBeforeCall;

    //then
    assertTrue("Checker should wait that long", timeSpent < waitingTime);
    assertDaoInsertUpdateCalledWithRightParameters(newExpirationAt, 2);
  }

  @Test
  public void shouldThrowException_WhenAcquire_IfQuitTryingAfterReached()
      throws LockPersistenceException {
    //given
    long expiresAt = 3000L;
    long waitingTime = quitTryingAfterMillis + 1;
    doThrow(new LockPersistenceException("acquireLockQuery", "newLockEntity", "dbErrorDetail")).when(lockRepository).insertUpdate(any(LockEntry.class));

    when(lockRepository.findByKey(anyString())).thenReturn(createFakeLockWithOtherOwner(expiresAt));
    Date newExpirationAt = new Date(100000L);
    when(timeUtils.currentDatePlusMillis(anyLong())).thenReturn(newExpirationAt);
    when(timeUtils.currentTime()).thenReturn(new Date(expiresAt - waitingTime));
    when(timeUtils.nowPlusMillis(anyLong())).thenReturn(DONT_CARE_INSTANT);
    when(timeUtils.isPast(any(Instant.class))).thenReturn(true);

    //when
    long timeBeforeCall = System.currentTimeMillis();
    try {
      lockManager.acquireLockDefault();
    } catch (LockCheckException ex) {
      //then
      assertTrue((System.currentTimeMillis() - timeBeforeCall) < waitingTime);
      assertDaoInsertUpdateCalledWithRightParameters(newExpirationAt, 1);
      assertExceptionMessage(ex);
      return;
    }
    fail();
  }

  //
  @Test
  public void shouldKeepTryingToAcquireLock_whenAcquire_WhileQuitTryingAfterNotReached() throws LockPersistenceException {
    //given
    long expiresAt = 3000L;
    long waitingTime = 1;
    doThrow(new LockPersistenceException("acquireLockQuery", "newLockEntity", "dbErrorDetail")).when(lockRepository).insertUpdate(any(LockEntry.class));

    when(lockRepository.findByKey(anyString())).thenReturn(createFakeLockWithOtherOwner(expiresAt));
    Date newExpirationAt = new Date(100000L);
    when(timeUtils.currentDatePlusMillis(anyLong())).thenReturn(newExpirationAt);
    when(timeUtils.currentTime()).thenReturn(new Date(expiresAt - waitingTime));
    when(timeUtils.nowPlusMillis(anyLong())).thenReturn(DONT_CARE_INSTANT);
    doReturn(false, false, true).when(timeUtils).isPast(any(Instant.class));

    // when
    try {
      lockManager.acquireLockDefault();
    } catch (LockCheckException ex) {
      //then
      assertDaoInsertUpdateCalledWithRightParameters(newExpirationAt, 3);
      assertExceptionMessage(ex);
      return;
    }
    fail();
  }


  @Test
  public void ensureLockShouldCallDaoFirstTime() throws LockPersistenceException, LockCheckException {
    //given
    Date expirationAt = new Date(1000L);
    when(timeUtils.currentDatePlusMillis(anyLong())).thenReturn(expirationAt);

    // when
    lockManager.ensureLockDefault();

    //then
    assertDaoUpdateIfSameOwnerCalledWithRightParameters(expirationAt, 1);
  }

  @Test
  public void shouldRefreshLock_IfLockIsExpired_whenEnsureLock() throws LockPersistenceException, LockCheckException {
    //given
    Date expirationAt = new Date(20 * 1000L);
    when(timeUtils.currentDatePlusMillis(anyLong())).thenReturn(expirationAt);
    when(timeUtils.currentTime()).thenReturn(new Date(40000L));// Exactly the expiration time(minus margin)

    lockManager.setLockAcquiredForMillis(3 * 1000L);// 3 seconds. Margin should 1 second
    lockManager.acquireLockDefault();

    // when
    lockManager.ensureLockDefault();

    //then
    assertDaoUpdateIfSameOwnerCalledWithRightParameters(expirationAt, 1);
  }

  //
  @Test
  public void shouldNotRefreshLock_IfAlreadyAcquired_whenEnsureLock() throws LockPersistenceException, LockCheckException {
    //given
    Date expirationAt = new Date(42 * 1000L);
    when(timeUtils.currentDatePlusMillis(anyLong())).thenReturn(expirationAt);
    when(timeUtils.currentTime()).thenReturn(new Date(39999L));// 1ms less than the expiration time(minus margin)

    lockManager.setLockAcquiredForMillis(3 * 1000L);// 3 seconds. Margin should 1 second
    lockManager.acquireLockDefault();

    // when
    lockManager.ensureLockDefault();

    //then
    assertDaoUpdateIfSameOwnerCalledWithRightParameters(expirationAt, 0);
  }

  @Test(expected = LockCheckException.class)
  public void ensureLockShouldThrowExceptionWhenDaoThrowsExceptionAndLockHeldByOther() throws LockPersistenceException, LockCheckException {
    //given
    long expiresAt = 3000L;
    long waitingTime = 1000L;

    doNothing().when(lockRepository).insertUpdate(any(LockEntry.class));
    doThrow(new LockPersistenceException("acquireLockQuery", "newLockEntity", "dbErrorDetail")).doNothing().when(lockRepository).updateIfSameOwner(any(LockEntry.class));

    when(lockRepository.findByKey(anyString())).thenReturn(createFakeLockWithOtherOwner(expiresAt));
    Date newExpirationAt = new Date(100000L);
    when(timeUtils.currentDatePlusMillis(anyLong()))
        .thenReturn(newExpirationAt)
        .thenReturn(newExpirationAt);
    when(timeUtils.currentTime())
        .thenReturn(new Date(40001L))
        .thenReturn(new Date(expiresAt - waitingTime));
    lockManager.acquireLockDefault();

    // when
    lockManager.ensureLockDefault();

  }

  @Test
  public void shouldTryAgain_IfNeedsRefresh_whenEnsureLock() throws LockPersistenceException, LockCheckException {
    //given
    long expiresAt = 3000L;
    long waitingTime = 1000L;
    doNothing().when(lockRepository).insertUpdate(any(LockEntry.class));
    doThrow(new LockPersistenceException("acquireLockQuery", "newLockEntity", "dbErrorDetail")).doNothing()
        .when(lockRepository).updateIfSameOwner(any(LockEntry.class));

    when(lockRepository.findByKey(anyString()))
        .thenReturn(new LockEntry(lockManager.getDefaultKey(), LockStatus.LOCK_HELD.name(), lockManager.getOwner(), new Date(expiresAt)));

    Date newExpirationAt = new Date(40000L);
    when(timeUtils.currentDatePlusMillis(anyLong())).thenReturn(newExpirationAt);
    when(timeUtils.currentTime())
        .thenReturn(new Date(40000L))
        .thenReturn(new Date(expiresAt - waitingTime));
    lockManager.setLockAcquiredForMillis(3 * 1000L);// 3 seconds. Margin should 1 second

    lockManager.acquireLockDefault();

    // when
    long timeBeforeCall = System.currentTimeMillis();
    lockManager.ensureLockDefault();
    long timeSpent = System.currentTimeMillis() - timeBeforeCall;

    //then
    assertTrue("Checker should wait that long", timeSpent < waitingTime);
    assertDaoUpdateIfSameOwnerCalledWithRightParameters(newExpirationAt, 1);
  }

  @Test
  public void shouldStopTrying_ifQuitTryingIsOver_WhenEnsureLock() throws LockPersistenceException {
    //given
    long expiresAt = 3000L;
    long waitingTime = 1;
    doThrow(new LockPersistenceException("acquireLockQuery", "newLockEntity", "dbErrorDetail")).when(lockRepository).updateIfSameOwner(any(LockEntry.class));
    when(lockRepository.findByKey(anyString())).thenReturn(createFakeLockWithSameOwner(expiresAt));
    Date newExpirationAt = new Date(100000L);
    when(timeUtils.currentDatePlusMillis(anyLong())).thenReturn(newExpirationAt);
    when(timeUtils.currentTime()).thenReturn(new Date(expiresAt - waitingTime));

    when(timeUtils.nowPlusMillis(anyLong())).thenReturn(DONT_CARE_INSTANT);
    doReturn(false, false, true)
        .when(timeUtils).isPast(any(Instant.class));

    // when
    try {
      lockManager.ensureLockDefault();
    } catch (LockCheckException ex) {
      assertDaoUpdateIfSameOwnerCalledWithRightParameters(newExpirationAt, 3);
      return;
    }
    fail();

  }

  @Test
  public void releaseLockCallDaoAlways() {
    //when
    lockManager.releaseLockDefault();

    //then
    verify(lockRepository).removeByKeyAndOwner(lockManager.getDefaultKey(), lockManager.getOwner());
  }

  @Test
  public void shouldHitTheDBAfterReleaseWhenAcquiringLock() throws LockPersistenceException, LockCheckException {
    //given
    when(timeUtils.currentDatePlusMillis(anyLong())).thenReturn(new Date(1000000L));
    when(timeUtils.currentTime()).thenReturn(new Date(0L));

    //when
    lockManager.acquireLockDefault();
    lockManager.releaseLockDefault();
    lockManager.acquireLockDefault();

    //then
    verify(lockRepository).removeByKeyAndOwner(lockManager.getDefaultKey(), lockManager.getOwner());
    verify(lockRepository, new Times(2)).insertUpdate(any(LockEntry.class));
  }

  @Test
  public void shouldHitTheDBAfterReleaseWhenEnsuringLock() throws LockPersistenceException, LockCheckException {
    //given
    when(timeUtils.currentDatePlusMillis(anyLong())).thenReturn(new Date(1000000L));
    when(timeUtils.currentTime()).thenReturn(new Date(0L));

    //when
    lockManager.acquireLockDefault();
    lockManager.releaseLockDefault();
    lockManager.ensureLockDefault();

    //then
    verify(lockRepository).removeByKeyAndOwner(lockManager.getDefaultKey(), lockManager.getOwner());
    verify(lockRepository, new Times(1)).updateIfSameOwner(any(LockEntry.class));
  }

  @Test
  public void getLockMaxTriesShouldReturnRight() {
    //given
    lockManager.setLockTryFrequencyMillis(3000L);

    //then
    assertEquals(3000L, lockManager.getLockTryFrequency());
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowIllegalExceptionWhenLockMaxWaitMillisLtOne() {
    lockManager.setLockQuitTryingAfterMillis(0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowIllegalException_WhenFrequencyIsLessOrEqualZero() {
    lockManager.setLockTryFrequencyMillis(0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowIllegalException_WhenAcquiredForLessThan3Seconds() {
    lockManager.setLockAcquiredForMillis(2999L);
  }

  @Test
  public void isLockHeldShouldReturnFalseWhenIsNotStarted() {
    //when
    boolean lockHeld = lockManager.isLockHeld();

    //then
    assertFalse(lockHeld);
  }

  @Test
  public void isLockHeldShouldReturnTrueWhenIsStarted() throws LockCheckException {
    //given
    when(timeUtils.currentDatePlusMillis(anyLong())).thenReturn(new Date(1000000L));
    when(timeUtils.currentTime()).thenReturn(new Date(0L));
    lockManager.acquireLockDefault();

    //when
    boolean lockHeld = lockManager.isLockHeld();

    //then
    assertTrue(lockHeld);
  }


  private static void assertExceptionMessage(LockCheckException ex) {
    assertEquals(
        "Quit trying lock after 180000 millis due to LockPersistenceException: \n" +
            "\tcurrent lock:  LockEntry{key='DEFAULT_LOCK', status='LOCK_HELD', owner='otherOwner', expiresAt=Thu Jan 01 00:00:03 WET 1970}\n" +
            "\tnew lock: newLockEntity\n" +
            "\tacquireLockQuery: acquireLockQuery\n" +
            "\tdb error detail: dbErrorDetail",
        ex.getMessage()
    );
  }
  private void assertDaoInsertUpdateCalledWithRightParameters(Date expirationAt, int invocationTimes)
      throws LockPersistenceException {
    assertDao(expirationAt, invocationTimes, false);
  }

  private void assertDaoUpdateIfSameOwnerCalledWithRightParameters(Date expirationAt, int invocationTimes)
      throws LockPersistenceException {
    assertDao(expirationAt, invocationTimes, true);
  }

  private void assertDao(Date expirationAt, int invocationTimes, boolean onlyIfSameOwner) throws LockPersistenceException {
    ArgumentCaptor<LockEntry> captor = ArgumentCaptor.forClass(LockEntry.class);
    if (onlyIfSameOwner) {
      verify(lockRepository, new Times(invocationTimes)).updateIfSameOwner(captor.capture());
    } else {
      verify(lockRepository, new Times(invocationTimes)).insertUpdate(captor.capture());
    }
    if (invocationTimes > 0) {
      LockEntry saved = captor.getValue();
      assertEquals("Lock was saved with the wrong key", lockManager.getDefaultKey(), saved.getKey());
      assertEquals("Lock was saved with the wrong status", LockStatus.LOCK_HELD.name(), saved.getStatus());
      assertEquals("lock was saved with the wrong owner", lockManager.getOwner(), saved.getOwner());
      assertEquals("Lock was saved with the wrong expires time", expirationAt, saved.getExpiresAt());
    }
  }

  private LockEntry createFakeLockWithOtherOwner(long expiresAt) {
    return createFakeLock(expiresAt, "otherOwner");
  }

  private LockEntry createFakeLockWithSameOwner(long expiresAt) {
    return createFakeLock(expiresAt, lockManager.getOwner());
  }

  private LockEntry createFakeLock(long expiresAt, String owner) {
    return new LockEntry(lockManager.getDefaultKey(), LockStatus.LOCK_HELD.name(), owner, new Date(expiresAt));
  }
}
