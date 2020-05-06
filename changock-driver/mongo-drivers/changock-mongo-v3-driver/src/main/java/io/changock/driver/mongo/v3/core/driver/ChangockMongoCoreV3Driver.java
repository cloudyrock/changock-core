package io.changock.driver.mongo.v3.core.driver;

import com.mongodb.client.MongoDatabase;
import io.changock.driver.api.driver.NotAllowedParameterMap;
import io.changock.driver.api.entry.ChangeEntry;
import io.changock.driver.api.entry.ChangeEntryService;
import io.changock.driver.mongo.v3.core.repository.MongoChangeEntryRepository;
import io.changock.utils.annotation.NotThreadSafe;

import java.util.HashMap;

@NotThreadSafe
public class ChangockMongoCoreV3Driver extends ChangockMongoCoreV3DriverBase<ChangeEntry> {

  private static final NotAllowedParameterMap notAllowedParameterMap = new NotAllowedParameterMap();

  protected MongoChangeEntryRepository<ChangeEntry> changeEntryRepository;


  public ChangockMongoCoreV3Driver(MongoDatabase mongoDatabase) {
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
