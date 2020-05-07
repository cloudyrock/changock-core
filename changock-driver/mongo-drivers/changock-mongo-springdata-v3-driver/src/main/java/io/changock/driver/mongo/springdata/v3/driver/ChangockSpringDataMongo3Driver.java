package io.changock.driver.mongo.springdata.v3.driver;

import io.changock.driver.api.driver.ChangeSetDependency;
import io.changock.driver.api.driver.ForbiddenParametersMap;
import io.changock.driver.api.lock.guard.invoker.LockGuardInvokerImpl;
import io.changock.driver.mongo.springdata.v3.driver.decorator.impl.MongockTemplate;
import io.changock.driver.mongo.syncv4.core.driver.ChangockMongoSync4Driver;
import io.changock.migration.api.exception.ChangockException;
import io.changock.utils.annotation.NotThreadSafe;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Set;

@NotThreadSafe
public class ChangockSpringDataMongo3Driver extends ChangockMongoSync4Driver {

  private static final ForbiddenParametersMap FORBIDDEN_PARAMETERS_MAP;

  private final MongoTemplate mongoTemplate;

  static {
    FORBIDDEN_PARAMETERS_MAP = new ForbiddenParametersMap();
    FORBIDDEN_PARAMETERS_MAP.put(MongoTemplate.class, MongockTemplate.class);
  }

  public ChangockSpringDataMongo3Driver(MongoTemplate mongoTemplate) {
    super(mongoTemplate.getDb());
    this.mongoTemplate = mongoTemplate;
  }

  @Override
  public void setChangeLogCollectionName(String changeLogCollectionName) {
    super.setChangeLogCollectionName(changeLogCollectionName);
  }

  @Override
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
