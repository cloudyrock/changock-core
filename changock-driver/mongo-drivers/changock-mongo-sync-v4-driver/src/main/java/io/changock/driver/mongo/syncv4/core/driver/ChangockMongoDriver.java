package io.changock.driver.mongo.syncv4.core.driver;

import com.mongodb.client.MongoDatabase;
import io.changock.driver.api.entry.ChangeEntry;
import io.changock.driver.api.entry.ChangeEntryService;
import io.changock.driver.mongo.syncv4.core.repository.MongoChangeEntryRepository;
import io.changock.utils.annotation.NotThreadSafe;

@NotThreadSafe
public class ChangockMongoDriver extends ChangockMongoDriverBase<ChangeEntry> {

  protected MongoChangeEntryRepository<ChangeEntry> changeEntryRepository;

  public ChangockMongoDriver(MongoDatabase mongoDatabase) {
    super(mongoDatabase);
  }

  @Override
  public ChangeEntryService<ChangeEntry> getChangeEntryService() {
    if (changeEntryRepository == null) {
      this.changeEntryRepository = new MongoChangeEntryRepository<>(mongoDatabase.getCollection(changeLogCollectionName));
    }
    return changeEntryRepository;
  }

}
