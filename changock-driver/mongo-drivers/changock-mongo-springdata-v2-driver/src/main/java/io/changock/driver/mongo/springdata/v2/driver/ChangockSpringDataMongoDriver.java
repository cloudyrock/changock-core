package io.changock.driver.mongo.springdata.v2.driver;

import io.changock.driver.api.driver.ChangeSetDependency;
import io.changock.driver.core.interceptor.decorator.LockMethodInvoker;
import io.changock.driver.mongo.springdata.v2.decorator.impl.MongoTemplateDecoratorImpl;
import io.changock.driver.mongo.v3.core.driver.ChangockMongoDriver;
import io.changock.migration.api.exception.ChangockException;
import io.changock.utils.annotation.NotThreadSafe;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Set;

@NotThreadSafe
public class ChangockSpringDataMongoDriver extends ChangockMongoDriver {


    private final MongoTemplate mongoTemplate;

    public ChangockSpringDataMongoDriver(MongoTemplate mongoTemplate) {
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
        //this is a workaround because MongoTemplate is a class with no empty-arguments constructor and uses executeCommand at construction time
        MongoTemplateDecoratorImpl.setDefaultMethodInvoker(new MongoTemplateInitializerLockMethodInvoker(this.getLockManager()));
        MongoTemplate mongoTemplateDecorator = new MongoTemplateDecoratorImpl(
                mongoTemplate.getMongoDbFactory(),
                mongoTemplate.getConverter(),
                new LockMethodInvoker(this.getLockManager()));
        dependencies.add(new ChangeSetDependency(MongoTemplate.class, mongoTemplateDecorator));
        return dependencies;
    }


}
