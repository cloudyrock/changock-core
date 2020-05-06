package io.changock.driver.mongo.syncv4.core.driver;

import com.mongodb.client.MongoDatabase;
import io.changock.driver.api.driver.NotAllowedParameterMap;
import io.changock.driver.api.entry.ChangeEntry;
import io.changock.driver.api.entry.ChangeEntryService;
import io.changock.driver.mongo.syncv4.core.repository.MongoChangeEntryRepository;
import io.changock.utils.annotation.NotThreadSafe;

@NotThreadSafe
public class ChangockMongoSyncV4Driver extends ChangockMongoSyncV4DriverBase<ChangeEntry> {

  private static final NotAllowedParameterMap notAllowedParameterMap = new NotAllowedParameterMap();

  protected MongoChangeEntryRepository<ChangeEntry> changeEntryRepository;

  public ChangockMongoSyncV4Driver(MongoDatabase mongoDatabase) {
    super(mongoDatabase);
  }

  @Override
  public ChangeEntryService<ChangeEntry> getChangeEntryService() {
    if (changeEntryRepository == null) {
      this.changeEntryRepository = new MongoChangeEntryRepository<>(mongoDatabase.getCollection(changeLogCollectionName));
    }
    return changeEntryRepository;
  }

  @Override
  public NotAllowedParameterMap notAllowedParameters() {
    return notAllowedParameterMap;
  }
}
