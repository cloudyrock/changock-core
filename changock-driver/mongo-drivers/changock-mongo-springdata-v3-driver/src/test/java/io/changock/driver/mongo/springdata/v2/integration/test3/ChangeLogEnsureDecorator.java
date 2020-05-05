package io.changock.driver.mongo.springdata.v2.integration.test3;

import com.mongodb.client.MongoDatabase;
import io.changock.driver.mongo.springdata.v3.driver.decorator.impl.MongockTemplate;
import io.changock.driver.mongo.springdata.v2.util.CallVerifier;
import io.changock.driver.mongo.v3.core.decorator.MongoDatabaseDecorator;
import io.changock.migration.api.annotations.ChangeLog;
import io.changock.migration.api.annotations.ChangeSet;
import org.junit.Assert;

@ChangeLog
public class ChangeLogEnsureDecorator {
  @ChangeSet(author = "testuser", id = "ensure_mongo_database", order = "00")
  public void ensureMongoDatabaseDecorator(MongoDatabase mongodatabase, CallVerifier callVerifier) {
    Assert.assertTrue(MongoDatabaseDecorator.class.isAssignableFrom(mongodatabase.getClass()));
    callVerifier.counter++;
  }

  @ChangeSet(author = "testuser", id = "ensure_mongo_template", order = "00")
  public void ensureMongoTemplateDecorator(MongockTemplate mongockTemplate, CallVerifier callVerifier) {
    Assert.assertTrue(MongockTemplate.class.isAssignableFrom(mongockTemplate.getClass()));
    callVerifier.counter++;
  }

}
