package io.changock.driver.mongo.v3.core.driver;

import com.mongodb.client.MongoDatabase;
import io.changock.driver.api.entry.ChangeEntry;
import io.changock.utils.annotation.NotThreadSafe;

@NotThreadSafe
public class ChangockMongoDriver extends ChangockMongoDriverBase<ChangeEntry> {

  public ChangockMongoDriver(MongoDatabase mongoDatabase) {
    super(mongoDatabase);
  }

}
