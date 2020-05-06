package io.changock.driver.mongo.springdata.v2.driver;

import io.changock.driver.api.driver.ChangeSetDependency;
import io.changock.driver.api.driver.ForbiddenParametersMap;
import io.changock.driver.api.lock.guard.invoker.LockGuardInvokerImpl;
import io.changock.driver.mongo.springdata.v2.driver.decorator.impl.MongockTemplate;
import io.changock.driver.mongo.v3.core.driver.ChangockMongoCoreV3Driver;
import io.changock.migration.api.exception.ChangockException;
import io.changock.utils.annotation.NotThreadSafe;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Set;

@NotThreadSafe
public class ChangockSpringDataMongoV2Driver extends ChangockMongoCoreV3Driver {

  private static final ForbiddenParametersMap FORBIDDEN_PARAMETERS_MAP;

  private final MongoTemplate mongoTemplate;

  static {
    FORBIDDEN_PARAMETERS_MAP = new ForbiddenParametersMap();
    FORBIDDEN_PARAMETERS_MAP.put(MongoTemplate.class, MongockTemplate.class);
  }

  public ChangockSpringDataMongoV2Driver(MongoTemplate mongoTemplate) {
    super(mongoTemplate.getDb());
    this.mongoTemplate = mongoTemplate;
  }

  @Override
  public void setChangeLogCollectionName(String changeLogCollectionName) {
    super.setChangeLogCollectionName(changeLogCollectionName);
  }

  public void setLockCollectionName(String lockCollectionName) {
    super.setLockCollectionName(lockCollectionName);
  }

  @Override
  public void runValidation() throws ChangockException {
    super.runValidation();

    if (this.mongoTemplate == null) {
      throw new ChangockException("MongoTemplate must not be null");
    }
  }

  @Override
  public Set<ChangeSetDependency> getDependencies() {
    Set<ChangeSetDependency> dependencies = super.getDependencies();
    dependencies.add(new ChangeSetDependency(MongockTemplate.class, new MongockTemplate(mongoTemplate, new LockGuardInvokerImpl(this.getLockManager()))));
    return dependencies;
  }

  @Override
  public ForbiddenParametersMap getForbiddenParameters() {
    return FORBIDDEN_PARAMETERS_MAP;
  }


}
