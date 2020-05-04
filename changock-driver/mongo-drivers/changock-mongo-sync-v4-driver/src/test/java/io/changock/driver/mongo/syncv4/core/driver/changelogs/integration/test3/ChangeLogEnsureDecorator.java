package io.changock.driver.mongo.syncv4.core.driver.changelogs.integration.test3;

import com.mongodb.client.MongoDatabase;
import io.changock.driver.mongo.syncv4.core.decorator.MongoDatabaseDecorator;
import io.changock.driver.mongo.syncv4.core.driver.util.CallVerifier;
import io.changock.migration.api.annotations.ChangeLog;
import io.changock.migration.api.annotations.ChangeSet;
import org.junit.Assert;

@ChangeLog
public class ChangeLogEnsureDecorator {


  @ChangeSet(author = "testuser", id = "id_duplicated", order = "00")
  public void method(MongoDatabase mongodatabase, CallVerifier callVerifier) {
    Assert.assertTrue(MongoDatabaseDecorator.class.isAssignableFrom(mongodatabase.getClass()));
    callVerifier.counter++;
  }

}
