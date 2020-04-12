package io.changock.driver.mongo.springdata.v2.decorator;

import com.mongodb.ClientSessionOptions;
import com.mongodb.DB;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoDatabase;
import io.changock.driver.mongo.springdata.v2.decorator.impl.MongoDbFactoryDecoratorImpl;
import io.changock.driver.mongo.springdata.v2.decorator.util.MongockDecoratorBase;
import io.changock.driver.mongo.v3.core.decorator.impl.MongoDataBaseDecoratorImpl;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.data.mongodb.MongoDbFactory;

public interface MongoDbFactoryDecorator extends MongockDecoratorBase<MongoDbFactory>, MongoDbFactory {

    @Override
    default MongoDatabase getDb() throws DataAccessException {
        return new MongoDataBaseDecoratorImpl(getImpl().getDb(), getInvoker());
    }

    @Override
    default MongoDatabase getDb(String dbName) throws DataAccessException {
        return new MongoDataBaseDecoratorImpl(getImpl().getDb(dbName), getInvoker());
    }

    @Override
    default PersistenceExceptionTranslator getExceptionTranslator() {
        return getImpl().getExceptionTranslator();
    }

    @Override
    default DB getLegacyDb() {
        throw new UnsupportedOperationException("Removed DB support from Mongock due to deprecated API. Please use MongoDatabase instead");
    }


    //TODO implement ClientSessionDecorator
    @Override
    default ClientSession getSession(ClientSessionOptions clientSessionOptions) {
        return getInvoker().invoke(() -> getImpl().getSession(clientSessionOptions));
    }

    @Override
    default MongoDbFactory withSession(ClientSession clientSession) {
        return getInvoker().invoke(() -> new MongoDbFactoryDecoratorImpl(getImpl().withSession(clientSession), getInvoker()));
    }
}
