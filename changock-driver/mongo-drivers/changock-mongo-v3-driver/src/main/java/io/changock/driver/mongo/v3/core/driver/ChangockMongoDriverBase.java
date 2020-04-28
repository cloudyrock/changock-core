package io.changock.driver.mongo.v3.core.driver;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.changock.driver.api.driver.ChangeSetDependency;
import io.changock.driver.api.entry.ChangeEntry;
import io.changock.driver.api.lock.LockManager;
import io.changock.driver.core.driver.ConnectionDriverBase;
import io.changock.driver.core.lock.LockRepository;
import io.changock.driver.core.lock.guard.invoker.LockGuardInvokerImpl;
import io.changock.driver.mongo.v3.core.decorator.impl.MongoDataBaseDecoratorImpl;
import io.changock.driver.mongo.v3.core.repository.MongoLockRepository;
import io.changock.migration.api.exception.ChangockException;
import io.changock.utils.annotation.NotThreadSafe;
import org.bson.Document;

import java.util.HashSet;
import java.util.Set;

@NotThreadSafe
public abstract class ChangockMongoDriverBase<CHANGE_ENTRY extends ChangeEntry> extends ConnectionDriverBase<CHANGE_ENTRY> {

  private static final String DEFAULT_CHANGELOG_COLLECTION_NAME = "changockChangeLog";
  private final static String DEFAULT_LOCK_COLLECTION_NAME = "changockLock";

  protected final MongoDatabase mongoDatabase;
  protected String changeLogCollectionName = DEFAULT_CHANGELOG_COLLECTION_NAME;
  protected String lockCollectionName = DEFAULT_LOCK_COLLECTION_NAME;
  protected MongoLockRepository lockRepository;

  public ChangockMongoDriverBase(MongoDatabase mongoDatabase) {
    this.mongoDatabase = mongoDatabase;
  }

  public void setChangeLogCollectionName(String changeLogCollectionName) {
    this.changeLogCollectionName = changeLogCollectionName;
  }

  public void setLockCollectionName(String lockCollectionName) {
    this.lockCollectionName = lockCollectionName;
  }

  @Override
  public void runValidation() throws ChangockException {
    if (mongoDatabase == null) {
      throw new ChangockException("MongoDatabase cannot be null");
    }
    if (this.getLockManager() == null) {
      throw new ChangockException("Internal error: Driver needs to be initialized by the runner");
    }
  }

  @Override
  protected LockRepository getLockRepository() {
    if (lockRepository == null) {
      MongoCollection<Document> collection = mongoDatabase.getCollection(lockCollectionName);
      this.lockRepository = new MongoLockRepository(collection);
    }
    return lockRepository;
  }

  @Override
  public Set<ChangeSetDependency> getDependencies() {
    LockManager lockManager = this.getLockManager();
    LockGuardInvokerImpl invoker = new LockGuardInvokerImpl(lockManager);
    Set<ChangeSetDependency> dependencies = new HashSet<>();
    MongoDataBaseDecoratorImpl mongoDataBaseDecorator = new MongoDataBaseDecoratorImpl(mongoDatabase, invoker);
    dependencies.add(new ChangeSetDependency(MongoDatabase.class, mongoDataBaseDecorator));
    return dependencies;
  }
}
