package io.changock.driver.mongo.springdata.v2.integration.test3;

import com.mongodb.client.MongoDatabase;
import io.changock.driver.mongo.springdata.v2.decorator.impl.MongoTemplateDecoratorImpl;
import io.changock.driver.mongo.springdata.v2.util.CallVerifier;
import io.changock.driver.mongo.v3.core.decorator.MongoDatabaseDecorator;
import io.changock.migration.api.annotations.ChangeLog;
import io.changock.migration.api.annotations.ChangeSet;
import org.junit.Assert;
import org.springframework.data.mongodb.core.MongoTemplate;

@ChangeLog
public class ChangeLogEnsureDecorator {
  @ChangeSet(author = "testuser", id = "ensure_mongo_database", order = "00")
  public void ensureMongoDatabaseDecorator(MongoDatabase mongodatabase, CallVerifier callVerifier) {
    Assert.assertTrue(MongoDatabaseDecorator.class.isAssignableFrom(mongodatabase.getClass()));
    callVerifier.counter++;
  }

  @ChangeSet(author = "testuser", id = "ensure_mongo_template", order = "00")
  public void ensureMongoTemplateDecorator(MongoTemplate mongoTemplate, CallVerifier callVerifier) {
    Assert.assertTrue(MongoTemplateDecoratorImpl.class.isAssignableFrom(mongoTemplate.getClass()));
    callVerifier.counter++;
  }

}
