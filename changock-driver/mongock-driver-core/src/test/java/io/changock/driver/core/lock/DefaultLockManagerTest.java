package io.changock.driver.core.lock;

import io.changock.driver.api.lock.LockCheckException;
import io.changock.driver.api.lock.LockManager;
import io.changock.utils.TimeService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.internal.verification.Times;

import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DefaultLockManagerTest {

  private static final long lockActiveMillis = 5 * 60 * 1000;
  private static final long maxWaitMillis = 60 * 1000;
  private static final int lockMaxTries = 3;

  private LockRepository lockRepository;
  private TimeService timeUtils;
  private LockManager lockManager;

  @Before
  public void setUp() {
    lockManager = new DefaultLockManager(lockRepository = Mockito.mock(LockRepository.class), timeUtils = Mockito.mock(TimeService.class))
        .setLockAcquiredForMillis(lockActiveMillis)
        .setLockMaxTries(lockMaxTries)
        .setLockMaxWaitMillis(maxWaitMillis);
  }

  @Test
  public void acquireLockShouldCallDaoFirstTime() throws LockPersistenceException, LockCheckException {
    //given
    Date expirationAt = new Date(1000L);
    when(timeUtils.currentTimePlusMillis(anyLong())).thenReturn(expirationAt);

    // when
    lockManager.acquireLockDefault();

    //then
    assertDaoInsertUpdateCalledWithRightParameters(expirationAt, 1);
  }

  @Test
  public void acquireLockShouldCallDaoSecondTimeWhenTimeHasAlreadyExpired() throws LockPersistenceException, LockCheckException {
    //given
    Date expirationAt = new Date(1000L);
    when(timeUtils.currentTimePlusMillis(anyLong())).thenReturn(expirationAt);
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
    when(timeUtils.currentTimePlusMillis(anyLong())).thenReturn(expirationAt);
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
    when(timeUtils.currentTimePlusMillis(anyLong())).thenReturn(newExpirationAt);
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
    lockManager.setLockMaxTries(1);
    long expiresAt = 3000L;
    long waitingTime = 1000L;
    doThrow(new LockPersistenceException("acquireLockQuery", "newLockEntity", "dbErrorDetail"))
        .doNothing()
        .when(lockRepository).insertUpdate(any(LockEntry.class));

    when(lockRepository.findByKey(anyString())).thenReturn(createFakeLockWithOtherOwner(expiresAt));
    Date newExpirationAt = new Date(100000L);
    when(timeUtils.currentTimePlusMillis(anyLong())).thenReturn(newExpirationAt);
    when(timeUtils.currentTime()).thenReturn(new Date(expiresAt - waitingTime));

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
    assertTrue("Checker should not wait at all " + waitingTime + "ms", timeSpent <= maxWaitMillis);
    assertDaoInsertUpdateCalledWithRightParameters(newExpirationAt, 1);
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
    when(timeUtils.currentTimePlusMillis(anyLong())).thenReturn(newExpirationAt);
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
  public void acquireLockShouldNotWaitButThrowExceptionWhenDaoThrowsExceptionAndLockIsHeldByOtherAndWaitingTimeIsGTMaxWaitMillis()
      throws LockPersistenceException {
    //given
    long expiresAt = 3000L;
    long waitingTime = maxWaitMillis + 1;
    doThrow(new LockPersistenceException("acquireLockQuery", "newLockEntity", "dbErrorDetail")).when(lockRepository).insertUpdate(any(LockEntry.class));

    when(lockRepository.findByKey(anyString())).thenReturn(createFakeLockWithOtherOwner(expiresAt));
    Date newExpirationAt = new Date(100000L);
    when(timeUtils.currentTimePlusMillis(anyLong())).thenReturn(newExpirationAt);
    when(timeUtils.currentTime())
        .thenReturn(new Date(expiresAt - waitingTime));

    //when
    long timeBeforeCall = System.currentTimeMillis();
    boolean exceptionThrown = false;
    try {
      lockManager.acquireLockDefault();
    } catch (LockCheckException ex) {
      exceptionThrown = true;
    }

    //then
    assertTrue("LockCheckException should be thrown", exceptionThrown);
    long timeSpent = System.currentTimeMillis() - timeBeforeCall;

    //then
    assertTrue(timeSpent < waitingTime);
    assertDaoInsertUpdateCalledWithRightParameters(newExpirationAt, 1);
  }

  @Test
  public void acquireLockShouldNotTryMoreThenMaxWhenDaoThrowsExceptionAndLockIsHeldByOther() throws LockPersistenceException {
    //given
    long expiresAt = 3000L;
    long waitingTime = 1;
    doThrow(new LockPersistenceException("acquireLockQuery", "newLockEntity", "dbErrorDetail")).when(lockRepository).insertUpdate(any(LockEntry.class));

    when(lockRepository.findByKey(anyString())).thenReturn(createFakeLockWithOtherOwner(expiresAt));
    Date newExpirationAt = new Date(100000L);
    when(timeUtils.currentTimePlusMillis(anyLong())).thenReturn(newExpirationAt);
    when(timeUtils.currentTime()).thenReturn(new Date(expiresAt - waitingTime));

    // when
    boolean exceptionThrown = false;
    try {
      lockManager.acquireLockDefault();
    } catch (LockCheckException ex) {
      exceptionThrown = true;
    }

    //then
    assertTrue("LockCheckException should be thrown", exceptionThrown);
    assertDaoInsertUpdateCalledWithRightParameters(newExpirationAt, 3);
  }

  @Test
  public void ensureLockShouldCallDaoFirstTime() throws LockPersistenceException, LockCheckException {
    //given
    Date expirationAt = new Date(1000L);
    when(timeUtils.currentTimePlusMillis(anyLong())).thenReturn(expirationAt);

    // when
    lockManager.ensureLockDefault();

    //then
    assertDaoUpdateIfSameOwnerCalledWithRightParameters(expirationAt, 1);
  }

  @Test
  public void ensureLockShouldCallDaoSecondTimeWhenTimeHasAlreadyExpired() throws LockPersistenceException, LockCheckException {
    //given
    Date expirationAt = new Date(100000L);
    when(timeUtils.currentTimePlusMillis(anyLong())).thenReturn(expirationAt);
    when(timeUtils.currentTime()).thenReturn(new Date(40000L));// Exactly the expiration time(minus margin)
    lockManager.acquireLockDefault();

    // when
    lockManager.ensureLockDefault();

    //then
    assertDaoUpdateIfSameOwnerCalledWithRightParameters(expirationAt, 1);
  }

  @Test
  public void ensureLockShouldNotCallDaoSecondTimeWhenTimeHasNotExpiredYet() throws LockPersistenceException, LockCheckException {
    //given
    Date expirationAt = new Date(100000L);
    when(timeUtils.currentTimePlusMillis(anyLong())).thenReturn(expirationAt);
    when(timeUtils.currentTime()).thenReturn(new Date(39999L));// 1ms less than the expiration time(minus margin)
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
    when(timeUtils.currentTimePlusMillis(anyLong()))
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
  public void ensureLockShouldTryAgainWhenDaoThrowsExceptionButLockHeldByTheSameOwner() throws LockPersistenceException, LockCheckException {
    //given
    long expiresAt = 3000L;
    long waitingTime = 1000L;
    doNothing().when(lockRepository).insertUpdate(any(LockEntry.class));
    doThrow(new LockPersistenceException("acquireLockQuery", "newLockEntity", "dbErrorDetail")).doNothing()
        .when(lockRepository).updateIfSameOwner(any(LockEntry.class));

    when(lockRepository.findByKey(anyString()))
        .thenReturn(new LockEntry(lockManager.getDefaultKey(), LockStatus.LOCK_HELD.name(), lockManager.getOwner(), new Date(expiresAt)));
    
    Date newExpirationAt = new Date(100000L);
    when(timeUtils.currentTimePlusMillis(anyLong())).thenReturn(newExpirationAt);
    when(timeUtils.currentTime())
        .thenReturn(new Date(40000L))
        .thenReturn(new Date(expiresAt - waitingTime));
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
  public void ensureLockShouldNotTryMoreThanMaxWhenDaoThrowsException() throws LockPersistenceException {
    //given
    long expiresAt = 3000L;
    long waitingTime = 1;
    doThrow(new LockPersistenceException("acquireLockQuery", "newLockEntity", "dbErrorDetail")).when(lockRepository).updateIfSameOwner(any(LockEntry.class));
    when(lockRepository.findByKey(anyString())).thenReturn(createFakeLockWithSameOwner(expiresAt));
    Date newExpirationAt = new Date(100000L);
    when(timeUtils.currentTimePlusMillis(anyLong())).thenReturn(newExpirationAt);
    when(timeUtils.currentTime())
        .thenReturn(new Date(expiresAt - waitingTime));

    // when
    boolean exceptionThrown = false;
    try {
      lockManager.ensureLockDefault();
    } catch (LockCheckException ex) {
      exceptionThrown = true;
    }

    //then
    assertTrue("LockCheckException should be thrown", exceptionThrown);
    assertDaoUpdateIfSameOwnerCalledWithRightParameters(newExpirationAt, 3);
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
    when(timeUtils.currentTimePlusMillis(anyLong())).thenReturn(new Date(1000000L));
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
    when(timeUtils.currentTimePlusMillis(anyLong())).thenReturn(new Date(1000000L));
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
    lockManager.setLockMaxTries(3);

    //when
    int lockMaxTries = lockManager.getLockMaxTries();

    //then
    assertEquals(3, lockMaxTries);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowIllegalExceptionWhenLockMaxWaitMillisLtOne() {
    lockManager.setLockMaxWaitMillis(0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowIllegalExceptionWhenLockMaxTriesLtOne() {
    lockManager.setLockMaxTries(0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowIllegalExceptionWhenAcquiredForLessThan2Minutes() {
    lockManager.setLockAcquiredForMillis(new TimeService().minutesToMillis(1) + 59);
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
    when(timeUtils.currentTimePlusMillis(anyLong())).thenReturn(new Date(1000000L));
    when(timeUtils.currentTime()).thenReturn(new Date(0L));
    lockManager.acquireLockDefault();

    //when
    boolean lockHeld = lockManager.isLockHeld();

    //then
    assertTrue(lockHeld);
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
