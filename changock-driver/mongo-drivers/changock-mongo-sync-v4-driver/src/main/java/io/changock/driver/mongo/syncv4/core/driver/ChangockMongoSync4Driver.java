package io.changock.driver.mongo.syncv4.core.driver;

import com.mongodb.client.MongoDatabase;
import io.changock.driver.api.driver.ForbiddenParametersMap;
import io.changock.driver.api.entry.ChangeEntry;
import io.changock.driver.api.entry.ChangeEntryService;
import io.changock.driver.mongo.syncv4.core.repository.MongoChangeEntryRepository;
import io.changock.utils.annotation.NotThreadSafe;

@NotThreadSafe
public class ChangockMongoSync4Driver extends ChangockMongoSync4DriverBase<ChangeEntry> {

  private static final ForbiddenParametersMap FORBIDDEN_PARAMETERS_MAP = new ForbiddenParametersMap();

  protected MongoChangeEntryRepository<ChangeEntry> changeEntryRepository;

  public ChangockMongoSync4Driver(MongoDatabase mongoDatabase) {
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
  public ForbiddenParametersMap getForbiddenParameters() {
    return FORBIDDEN_PARAMETERS_MAP;
  }
}
