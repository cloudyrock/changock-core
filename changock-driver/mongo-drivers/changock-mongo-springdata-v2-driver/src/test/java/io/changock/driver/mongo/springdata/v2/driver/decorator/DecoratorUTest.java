package io.changock.driver.mongo.springdata.v2.driver.decorator;

import com.mongodb.MongoNamespace;
import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import com.mongodb.ServerCursor;
import com.mongodb.WriteConcern;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import io.changock.driver.api.lock.LockManager;
import io.changock.driver.api.lock.guard.invoker.LockGuardInvokerImpl;
import io.changock.driver.mongo.springdata.v2.driver.decorator.impl.MongockTemplate;
import io.changock.test.util.decorator.DecoratorMethodFailure;
import io.changock.test.util.decorator.DecoratorTestCollection;
import io.changock.test.util.decorator.DecoratorValidator;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SessionScoped;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.mapreduce.GroupByResults;
import org.springframework.data.mongodb.core.mapreduce.MapReduceResults;
import org.springframework.data.mongodb.core.script.NamedMongoScript;
import org.springframework.data.util.CloseableIterator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;


//TODO generify this and move to testUtils lib for other projects
public class DecoratorUTest {


  private DecoratorTestCollection getDecoratorsToTest() {
    //executableFindOperations
    return new DecoratorTestCollection()
        //MongockTemplate
        .addDecorator(MongoOperations.class, MongockTemplate.class)
//        .addDecorator(IndexOperationsProvider.class, MongockTemplate.class, mongockTemplate)
//        .addDecorator(ExecutableFindOperation.ExecutableFind.class, ExecutableFindDecoratorImpl.class)
//        .addDecorator(ExecutableFindOperation.FindDistinct.class, FindDistinctDecoratorImpl.class)
//        .addDecorator(ExecutableFindOperation.FindWithCollection.class, FindWithCollectionDecoratorImpl.class)
//        .addDecorator(ExecutableFindOperation.FindWithProjection.class, FindWithProjectionDecoratorImpl.class)
//        .addDecorator(ExecutableFindOperation.FindWithQuery.class, FindWithQueryDecoratorImpl.class)
//        .addDecorator(ExecutableFindOperation.TerminatingFind.class, TerminatingFindDecoratorImpl.class)
//        .addDecorator(ExecutableFindOperation.TerminatingFindNear.class, TerminatingFindNearDecoratorImpl.class)
//        .addDecorator(ExecutableFindOperation.TerminatingDistinct.class, TerminatingDistinctDecoratorImpl.class)
//        //executableAggregation
//        .addDecorator(ExecutableAggregationOperation.AggregationWithCollection.class, AggregationWithCollectionDecoratorImpl.class)
//        .addDecorator(ExecutableAggregationOperation.AggregationWithAggregation.class, AggregationWithAggregationDecoratorImpl.class)
//        .addDecorator(ExecutableAggregationOperation.ExecutableAggregation.class, ExecutableAggregationDecoratorImpl.class)
//        .addDecorator(ExecutableAggregationOperation.TerminatingAggregation.class, TerminatingAggregationDecoratorImpl.class)
//        //executableInsert
//        .addDecorator(ExecutableInsertOperation.ExecutableInsert.class, ExecutableInsertDecoratorImpl.class)
//        .addDecorator(ExecutableInsertOperation.InsertWithBulkMode.class, InsertWithBulkModeDecoratorImpl.class)
//        .addDecorator(ExecutableInsertOperation.InsertWithCollection.class, InsertWithCollectionDecoratorImpl.class)
//        .addDecorator(ExecutableInsertOperation.TerminatingInsert.class, TerminatingInsertDecoratorImpl.class)
//        .addDecorator(ExecutableInsertOperation.TerminatingBulkInsert.class, TerminatingBulkInsertDecoratorImpl.class)
//        //executableRemove
//        .addDecorator(ExecutableRemoveOperation.ExecutableRemove.class, ExecutableRemoveDecoratorImpl.class)
//        .addDecorator(ExecutableRemoveOperation.RemoveWithCollection.class, RemoveWithCollectionDecoratorImpl.class)
//        .addDecorator(ExecutableRemoveOperation.RemoveWithQuery.class, RemoveWithQueryDecoratorImpl.class)
//        .addDecorator(ExecutableRemoveOperation.TerminatingRemove.class, TerminatingRemoveDecoratorImpl.class)
//        //executableUpdate
//        .addDecorator(ExecutableUpdateOperation.ExecutableUpdate.class, ExecutableUpdateDecoratorImpl.class)
//        .addDecorator(ExecutableUpdateOperation.UpdateWithQuery.class, UpdateWithQueryDecoratorImpl.class)
//        .addDecorator(ExecutableUpdateOperation.UpdateWithCollection.class, UpdateWithCollectionDecoratorImpl.class)
//        .addDecorator(ExecutableUpdateOperation.UpdateWithUpdate.class, UpdateWithUpdateDecoratorImpl.class)
//        .addDecorator(ExecutableUpdateOperation.FindAndReplaceWithProjection.class, FindAndReplaceWithProjectionDecoratorImpl.class)
//        .addDecorator(ExecutableUpdateOperation.TerminatingUpdate.class, TerminatingUpdateDecoratorImpl.class)
//        .addDecorator(ExecutableUpdateOperation.FindAndReplaceWithOptions.class, FindAndReplaceWithOptionsDecoratorImpl.class)
//        .addDecorator(ExecutableUpdateOperation.TerminatingFindAndModify.class, TerminatingFindAndModifyDecoratorImpl.class)
//        .addDecorator(ExecutableUpdateOperation.TerminatingFindAndReplace.class, TerminatingFindAndReplaceDecoratorImpl.class)
//        .addDecorator(ExecutableUpdateOperation.FindAndModifyWithOptions.class, FindAndModifyWithOptionsDecoratorImpl.class)
//        //executableMapReduce
//        .addDecorator(ExecutableMapReduceOperation.ExecutableMapReduce.class, ExecutableMapReduceDecoratorImpl.class)
//        .addDecorator(ExecutableMapReduceOperation.MapReduceWithOptions.class, MapReduceWithOptionsDecoratorImpl.class)
//        .addDecorator(ExecutableMapReduceOperation.MapReduceWithCollection.class, MapReduceWithCollectionDecoratorImpl.class)
//        .addDecorator(ExecutableMapReduceOperation.MapReduceWithReduceFunction.class, MapReduceWithReduceFunctionDecoratorImpl.class)
//        .addDecorator(ExecutableMapReduceOperation.MapReduceWithMapFunction.class, MapReduceWithMapFunctionDecoratorImpl.class)
//        .addDecorator(ExecutableMapReduceOperation.MapReduceWithProjection.class, MapReduceWithProjectionDecoratorImpl.class)
//        .addDecorator(ExecutableMapReduceOperation.MapReduceWithQuery.class, MapReduceWithQueryDecoratorImpl.class)
//        .addDecorator(ExecutableMapReduceOperation.TerminatingMapReduce.class, TerminatingMapReduceDecoratorImpl.class)
//        //basic
//        .addDecorator(BulkOperations.class, BulkOperationsDecoratorImpl.class)
//        .addDecorator(ClientSession.class, ClientSessionDecoratorImpl.class, "getPinnedServerAddress", "setPinnedServerAddress", "hasActiveTransaction", "notifyMessageSent", "getTransactionOptions")
//        .addDecorator(CloseableIterator.class, CloseableIteratorDecoratorImpl.class)
//        .addDecorator(IndexOperations.class, IndexOperationsDecoratorImpl.class)
//        .addDecorator(ScriptOperations.class, ScriptOperationsDecoratorImpl.class)
//        .addDecorator(SessionCallback.class, SessionCallbackDecoratorImpl.class)
//        .addDecorator(SessionScoped.class, SessionScopedDecoratorImpl.class, "lambda$execute$0")
        ;

  }


  @Test
  public void allMethodsInDecoratorsShouldEnsureLockAndReturnDecoratorIfNotTerminatingOperations() {
    LockManager lockManager = Mockito.mock(LockManager.class);
    List<DecoratorMethodFailure> failedDecorators = new DecoratorValidator(
        getDecoratorsToTest(),
        getIgnoredTypes(),
        Arrays.asList(MongockTemplate.class),
        getInstancesMap(lockManager),
        lockManager)
        .checkAndReturnFailedDecorators();
    int size = failedDecorators.size();
    Assert.assertEquals(DecoratorMethodFailure.printErrorMessage(failedDecorators), 0, size);
  }

  private Map<Class, Object> getInstancesMap(LockManager lockManager) {
    Map<Class, Object> instancesMap = new HashMap<>();
    instancesMap.put(MongockTemplate.class, new MongockTemplate(Mockito.mock(MongoTemplate.class), new LockGuardInvokerImpl(lockManager)));
    return instancesMap;
  }

  private Collection<Class> getIgnoredTypes() {
    Collection<Class> ignored = new ArrayList<>(Arrays.asList(
        Document.class
        , MongoConverter.class
        , GroupByResults.class
        , DeleteResult.class
        , AggregationResults.class
        , GeoResults.class
        , GeoResult.class
        , UpdateResult.class
        , MapReduceResults.class
        ,CloseableIterator.class
        , MongoNamespace.class
        , CodecRegistry.class
        , ReadPreference.class
        , ReadConcern.class
        , WriteConcern.class
        , BulkWriteResult.class
        , NamedMongoScript.class
        , BsonDocument.class
        , ServerCursor.class
        , ServerAddress.class
        , Optional.class

        ,SessionScoped.class// TODO remove this
    ));
    ignored.addAll(javaTypes);
    return ignored;
  }

  //TODO class.isPrimitive()
  private Collection<Class> javaTypes = Arrays.asList(
      int.class,
      long.class,
      double.class,
      float.class,
      boolean.class,
      Integer.class,
      String.class,
      Long.class,
      Double.class,
      Float.class,
      Boolean.class,
      List.class,
      Collection.class,
      Map.class,
      HashMap.class,
      Set.class,
      HashSet.class,
      Stream.class,
      Object.class,
      Class.class

  );

}
