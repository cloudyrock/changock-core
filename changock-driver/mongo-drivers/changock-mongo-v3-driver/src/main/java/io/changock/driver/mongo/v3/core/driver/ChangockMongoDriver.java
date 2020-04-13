package io.changock.driver.mongo.v3.core.driver;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.changock.driver.api.driver.ChangeSetDependency;
import io.changock.driver.api.entry.ChangeEntry;
import io.changock.driver.api.entry.ChangeEntryService;
import io.changock.driver.api.lock.LockManager;
import io.changock.driver.core.decorator.LockMethodInvoker;
import io.changock.driver.core.driver.ConnectionDriverBase;
import io.changock.driver.core.lock.LockRepository;
import io.changock.driver.mongo.v3.core.decorator.impl.MongoDataBaseDecoratorImpl;
import io.changock.driver.mongo.v3.core.repository.MongoChangeEntryRepository;
import io.changock.driver.mongo.v3.core.repository.MongoLockRepository;
import io.changock.migration.api.exception.ChangockException;
import io.changock.utils.annotation.NotThreadSafe;
import org.bson.Document;

import java.util.HashSet;
import java.util.Set;

@NotThreadSafe
public class ChangockMongoDriver extends ConnectionDriverBase<ChangeEntry> {

    private static final String DEFAULT_CHANGELOG_COLLECTION_NAME = "changockChangeLog";
    private final static String DEFAULT_LOCK_COLLECTION_NAME = "changockLock";

    private final MongoDatabase mongoDatabase;
    private String changeLogCollectionName = DEFAULT_CHANGELOG_COLLECTION_NAME;
    private String lockCollectionName = DEFAULT_LOCK_COLLECTION_NAME;
    private MongoChangeEntryRepository changeEntryRepository;
    private MongoLockRepository lockRepository;

    public ChangockMongoDriver(MongoDatabase mongoDatabase) {
        this.mongoDatabase = mongoDatabase;
    }

    public ChangockMongoDriver setChangeLogCollectionName(String changeLogCollectionName) {
        this.changeLogCollectionName = changeLogCollectionName;
        return this;
    }

    public ChangockMongoDriver setLockCollectionName(String lockCollectionName) {
        this.lockCollectionName = lockCollectionName;
        return this;
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
        LockMethodInvoker invoker = new LockMethodInvoker(lockManager);
        Set<ChangeSetDependency> dependencies = new HashSet<>();
        MongoDataBaseDecoratorImpl mongoDataBaseDecorator = new MongoDataBaseDecoratorImpl(mongoDatabase, invoker);
        dependencies.add(new ChangeSetDependency(MongoDatabase.class, mongoDataBaseDecorator));
        return dependencies;
    }

    @Override
    public ChangeEntryService getChangeEntryService() {
        if (changeEntryRepository == null) {
            this.changeEntryRepository = new MongoChangeEntryRepository(mongoDatabase.getCollection(changeLogCollectionName));
        }
        return changeEntryRepository;
    }
}
