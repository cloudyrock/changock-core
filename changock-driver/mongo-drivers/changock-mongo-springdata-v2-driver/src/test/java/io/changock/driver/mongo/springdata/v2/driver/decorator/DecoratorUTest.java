package io.changock.driver.mongo.springdata.v2.driver.decorator;

import io.changock.driver.api.lock.LockManager;
import io.changock.driver.core.lock.guard.invoker.LockGuardInvoker;
import io.changock.driver.core.lock.guard.invoker.LockGuardInvokerImpl;
import io.changock.driver.mongo.springdata.v2.driver.decorator.impl.BulkOperationsDecoratorImpl;
import io.changock.driver.mongo.springdata.v2.driver.decorator.impl.ClientSessionDecoratorImpl;
import io.changock.driver.mongo.springdata.v2.driver.decorator.impl.CloseableIteratorDecoratorImpl;
import io.changock.driver.mongo.springdata.v2.driver.decorator.impl.IndexOperationsDecoratorImpl;
import io.changock.driver.mongo.springdata.v2.driver.decorator.impl.MongockTemplate;
import io.changock.driver.mongo.springdata.v2.driver.decorator.impl.ScriptOperationsDecoratorImpl;
import io.changock.driver.mongo.springdata.v2.driver.decorator.impl.SessionCallbackDecoratorImpl;
import io.changock.driver.mongo.springdata.v2.driver.decorator.impl.SessionScopedDecoratorImpl;
import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.aggregation.impl.AggregationWithAggregationDecoratorImpl;
import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.aggregation.impl.AggregationWithCollectionDecoratorImpl;
import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.aggregation.impl.ExecutableAggregationDecoratorImpl;
import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.aggregation.impl.TerminatingAggregationDecoratorImpl;
import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.find.impl.FindDistinctDecoratorImpl;
import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.find.impl.FindWithCollectionDecoratorImpl;
import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.find.impl.FindWithProjectionDecoratorImpl;
import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.find.impl.FindWithQueryDecoratorImpl;
import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.find.impl.TerminatingDistinctDecoratorImpl;
import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.find.impl.TerminatingFindDecoratorImpl;
import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.find.impl.TerminatingFindNearDecoratorImpl;
import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.insert.impl.ExecutableInsertDecoratorImpl;
import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.insert.impl.InsertWithBulkModeDecoratorImpl;
import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.insert.impl.InsertWithCollectionDecoratorImpl;
import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.insert.impl.TerminatingBulkInsertDecoratorImpl;
import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.insert.impl.TerminatingInsertDecoratorImpl;
import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.mapreduce.impl.ExecutableMapReduceDecoratorImpl;
import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.mapreduce.impl.MapReduceWithCollectionDecoratorImpl;
import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.mapreduce.impl.MapReduceWithMapFunctionDecoratorImpl;
import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.mapreduce.impl.MapReduceWithOptionsDecoratorImpl;
import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.mapreduce.impl.MapReduceWithProjectionDecoratorImpl;
import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.mapreduce.impl.MapReduceWithQueryDecoratorImpl;
import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.mapreduce.impl.MapReduceWithReduceFunctionDecoratorImpl;
import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.mapreduce.impl.TerminatingMapReduceDecoratorImpl;
import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.remove.impl.ExecutableRemoveDecoratorImpl;
import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.remove.impl.RemoveWithCollectionDecoratorImpl;
import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.remove.impl.RemoveWithQueryDecoratorImpl;
import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.remove.impl.TerminatingRemoveDecoratorImpl;
import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.update.impl.ExecutableUpdateDecoratorImpl;
import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.update.impl.FindAndModifyWithOptionsDecoratorImpl;
import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.update.impl.FindAndReplaceWithOptionsDecoratorImpl;
import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.update.impl.FindAndReplaceWithProjectionDecoratorImpl;
import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.update.impl.TerminatingFindAndModifyDecoratorImpl;
import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.update.impl.TerminatingFindAndReplaceDecoratorImpl;
import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.update.impl.TerminatingUpdateDecoratorImpl;
import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.update.impl.UpdateWithCollectionDecoratorImpl;
import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.update.impl.UpdateWithQueryDecoratorImpl;
import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.update.impl.UpdateWithUpdateDecoratorImpl;
import com.mongodb.client.ClientSession;
import io.changock.driver.mongo.springdata.v2.driver.decorator.operation.executable.find.impl.ExecutableFindDecoratorImpl;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.Invocation;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.ExecutableAggregationOperation;
import org.springframework.data.mongodb.core.ExecutableFindOperation;
import org.springframework.data.mongodb.core.ExecutableInsertOperation;
import org.springframework.data.mongodb.core.ExecutableMapReduceOperation;
import org.springframework.data.mongodb.core.ExecutableRemoveOperation;
import org.springframework.data.mongodb.core.ExecutableUpdateOperation;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.ScriptOperations;
import org.springframework.data.mongodb.core.SessionCallback;
import org.springframework.data.mongodb.core.SessionScoped;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.index.IndexOperationsProvider;
import org.springframework.data.util.CloseableIterator;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;


//TODO generify this and move to testUtils lib for other projects
public class DecoratorUTest {

  private static LockManager lockManager;
  private static final Map<Class<?>, DecoratorDefinition> decorators;

  static {
    lockManager = Mockito.mock(LockManager.class);
    decorators = new HashMap<>();
    //executableFindOperations
    addDecorator(ExecutableFindOperation.ExecutableFind.class, ExecutableFindDecoratorImpl.class);
    addDecorator(ExecutableFindOperation.FindDistinct.class, FindDistinctDecoratorImpl.class);
    addDecorator(ExecutableFindOperation.FindWithCollection.class, FindWithCollectionDecoratorImpl.class);
    addDecorator(ExecutableFindOperation.FindWithProjection.class, FindWithProjectionDecoratorImpl.class);
    addDecorator(ExecutableFindOperation.FindWithQuery.class, FindWithQueryDecoratorImpl.class);
    addDecorator(ExecutableFindOperation.TerminatingFind.class, TerminatingFindDecoratorImpl.class);
    addDecorator(ExecutableFindOperation.TerminatingFindNear.class, TerminatingFindNearDecoratorImpl.class);
    addDecorator(ExecutableFindOperation.TerminatingDistinct.class, TerminatingDistinctDecoratorImpl.class);
    //executableAggregation
    addDecorator(ExecutableAggregationOperation.AggregationWithCollection.class, AggregationWithCollectionDecoratorImpl.class);
    addDecorator(ExecutableAggregationOperation.AggregationWithAggregation.class, AggregationWithAggregationDecoratorImpl.class);
    addDecorator(ExecutableAggregationOperation.ExecutableAggregation.class, ExecutableAggregationDecoratorImpl.class);
    addDecorator(ExecutableAggregationOperation.TerminatingAggregation.class, TerminatingAggregationDecoratorImpl.class);
    //executableInsert
    addDecorator(ExecutableInsertOperation.ExecutableInsert.class, ExecutableInsertDecoratorImpl.class);
    addDecorator(ExecutableInsertOperation.InsertWithBulkMode.class, InsertWithBulkModeDecoratorImpl.class);
    addDecorator(ExecutableInsertOperation.InsertWithCollection.class, InsertWithCollectionDecoratorImpl.class);
    addDecorator(ExecutableInsertOperation.TerminatingInsert.class, TerminatingInsertDecoratorImpl.class);
    addDecorator(ExecutableInsertOperation.TerminatingBulkInsert.class, TerminatingBulkInsertDecoratorImpl.class);
    //executableRemove
    addDecorator(ExecutableRemoveOperation.ExecutableRemove.class, ExecutableRemoveDecoratorImpl.class);
    addDecorator(ExecutableRemoveOperation.RemoveWithCollection.class, RemoveWithCollectionDecoratorImpl.class);
    addDecorator(ExecutableRemoveOperation.RemoveWithQuery.class, RemoveWithQueryDecoratorImpl.class);
    addDecorator(ExecutableRemoveOperation.TerminatingRemove.class, TerminatingRemoveDecoratorImpl.class);
    //executableUpdate
    addDecorator(ExecutableUpdateOperation.ExecutableUpdate.class, ExecutableUpdateDecoratorImpl.class);
    addDecorator(ExecutableUpdateOperation.UpdateWithQuery.class, UpdateWithQueryDecoratorImpl.class);
    addDecorator(ExecutableUpdateOperation.UpdateWithCollection.class, UpdateWithCollectionDecoratorImpl.class);
    addDecorator(ExecutableUpdateOperation.UpdateWithUpdate.class, UpdateWithUpdateDecoratorImpl.class);
    addDecorator(ExecutableUpdateOperation.FindAndReplaceWithProjection.class, FindAndReplaceWithProjectionDecoratorImpl.class);
    addDecorator(ExecutableUpdateOperation.TerminatingUpdate.class, TerminatingUpdateDecoratorImpl.class);
    addDecorator(ExecutableUpdateOperation.FindAndReplaceWithOptions.class, FindAndReplaceWithOptionsDecoratorImpl.class);
    addDecorator(ExecutableUpdateOperation.TerminatingFindAndModify.class, TerminatingFindAndModifyDecoratorImpl.class);
    addDecorator(ExecutableUpdateOperation.TerminatingFindAndReplace.class, TerminatingFindAndReplaceDecoratorImpl.class);
    addDecorator(ExecutableUpdateOperation.FindAndModifyWithOptions.class, FindAndModifyWithOptionsDecoratorImpl.class);
    //executableMapReduce
    addDecorator(ExecutableMapReduceOperation.ExecutableMapReduce.class, ExecutableMapReduceDecoratorImpl.class);
    addDecorator(ExecutableMapReduceOperation.MapReduceWithOptions.class, MapReduceWithOptionsDecoratorImpl.class);
    addDecorator(ExecutableMapReduceOperation.MapReduceWithCollection.class, MapReduceWithCollectionDecoratorImpl.class);
    addDecorator(ExecutableMapReduceOperation.MapReduceWithReduceFunction.class, MapReduceWithReduceFunctionDecoratorImpl.class);
    addDecorator(ExecutableMapReduceOperation.MapReduceWithMapFunction.class, MapReduceWithMapFunctionDecoratorImpl.class);
    addDecorator(ExecutableMapReduceOperation.MapReduceWithProjection.class, MapReduceWithProjectionDecoratorImpl.class);
    addDecorator(ExecutableMapReduceOperation.MapReduceWithQuery.class, MapReduceWithQueryDecoratorImpl.class);
    addDecorator(ExecutableMapReduceOperation.TerminatingMapReduce.class, TerminatingMapReduceDecoratorImpl.class);
    //basic
    addDecorator(BulkOperations.class, BulkOperationsDecoratorImpl.class);
    addDecorator(ClientSession.class, ClientSessionDecoratorImpl.class, "getPinnedServerAddress", "setPinnedServerAddress", "hasActiveTransaction", "notifyMessageSent", "getTransactionOptions");
    addDecorator(CloseableIterator.class, CloseableIteratorDecoratorImpl.class);
    addDecorator(IndexOperations.class, IndexOperationsDecoratorImpl.class);

//    addDecorator(MongoOperations.class, MongoOperationsDecoratorImpl.class);
    addDecorator(ScriptOperations.class, ScriptOperationsDecoratorImpl.class);
    addDecorator(SessionCallback.class, SessionCallbackDecoratorImpl.class);
    addDecorator(SessionScoped.class, SessionScopedDecoratorImpl.class, "lambda$execute$0");

    //MongockTemplate
    MongockTemplate mongockTemplate = new MongockTemplate(Mockito.mock(MongoTemplate.class), new LockGuardInvokerImpl(lockManager));
    addDecorator(MongoOperations.class, MongockTemplate.class, mongockTemplate, "getConverter", "getCollectionName");
    addDecorator(IndexOperationsProvider.class, MongockTemplate.class, mongockTemplate);


  }

  private static <T> void addDecorator(Class<T> interfaceType, Class<? extends T> implementingClass, String... noLockGardMethods) {
    decorators.put(interfaceType, new DecoratorDefinition(interfaceType, implementingClass, noLockGardMethods));
  }

  private static <T> void addDecorator(Class<T> interfaceType, Class<? extends T> implementingClass) {
    decorators.put(interfaceType, new DecoratorDefinition(interfaceType, implementingClass));
  }

  private static <T, R extends T> void addDecorator(Class<T> interfaceType, Class<R> implementingClass, R instance, String... noLockGardMethods) {
    decorators.put(interfaceType, new DecoratorDefinition(interfaceType, implementingClass, instance, noLockGardMethods));
  }

  @Test
  public void allMethodsInDecoratorsShouldEnsureLockAndReturnDecoratorIfNotTerminatingOperations() {
    List<DecoratorMethodFailure> failedDecorators = decorators.values()
        .stream()
        .map(decoratorDefinition -> getMethodErrorsFromDecorator(decoratorDefinition, decoratorDefinition.getInterfaceType()))
        .flatMap(Collection::stream)
        .collect(Collectors.toList());

    Assert.assertEquals("Decorator errors should be zero, but it's instead: " + failedDecorators.size(), Collections.emptyList(), failedDecorators);
  }


  private static Collection<DecoratorMethodFailure> getMethodErrorsFromDecorator(DecoratorDefinition decorator, Class interfaceType) {
    Method[] declaredMethods = interfaceType.getDeclaredMethods();
    Collection<DecoratorMethodFailure> methodFailures = Stream.of(declaredMethods)
        .map(method -> getMethodErrorOptional(method, decorator))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.toList());

    Collection<DecoratorMethodFailure> methodFailuresFromHigherInterfaces = Stream.of(interfaceType.getInterfaces())
        .filter(decorators::containsKey)//only interested in interfaces that are decorated
        .map(type -> getMethodErrorsFromDecorator(decorator, type))
        .flatMap(Collection::stream)
        .collect(Collectors.toList());
    methodFailures.addAll(methodFailuresFromHigherInterfaces);
    return methodFailures;
  }

  private static Optional<DecoratorMethodFailure> getMethodErrorOptional(Method method, DecoratorDefinition decorator) {
    try {
      method.setAccessible(true);
      boolean errorReturningDecorator = isErrorReturningDecorator(method, decorator, method.getReturnType());
      boolean errorEnsuringLock = isErrorEnsuringLock(method, decorator);
      return errorEnsuringLock || errorReturningDecorator
          ? Optional.of(new DecoratorMethodFailure(decorator.getImplementingType(), method, errorReturningDecorator, errorEnsuringLock))
          : Optional.empty();
    } catch (Exception ex) {
      return Optional.of(DecoratorMethodFailure.otherError(decorator.getImplementingType(), method, ex.getMessage() != null ? ex.getMessage() : ex.getClass().getSimpleName()));
    }
  }

  private static boolean isErrorEnsuringLock(Method method, DecoratorDefinition decorator) throws NoSuchMethodException {

    Collection<Invocation> invocations = Mockito.mockingDetails(lockManager).getInvocations();
    return !decorator.getNoLockGardMethods().contains(method.getName()) && (invocations.size() != 1 || !invocations.iterator()
        .next()
        .getMethod()
        .equals(LockManager.class.getMethod("ensureLockDefault")));
  }

  private static boolean isErrorReturningDecorator(Method method, DecoratorDefinition decorator, Class<?> returnType) throws Exception {
    Mockito.reset(lockManager);
    //method.invoke needs to be executed
    Object result = method.invoke(
        decorator.getInstance().orElseGet(() -> getDecoratorInstance(decorator, new LockGuardInvokerImpl(lockManager))),
        getNullParametersFromMethod(method));
    return !Void.TYPE.equals(returnType) && decorators.containsKey(returnType) && !isDecoratorImplementation(result);
  }


  @SuppressWarnings("unchecked")
  private static Object getDecoratorInstance(DecoratorDefinition decorator, LockGuardInvoker invokerMock) {
    try {
      Object instance =  decorator.getImplementingType()
          .getConstructor(decorator.getInterfaceType(), LockGuardInvoker.class)
          .newInstance(Mockito.mock(decorator.getImplementingType()), invokerMock);
      return instance;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static boolean isDecoratorImplementation(Object result) {
    return decorators.values().stream().map(DecoratorDefinition::getImplementingType).anyMatch(result.getClass()::equals);
  }

  @NotNull
  private static Object[] getNullParametersFromMethod(Method method) {
    Class[] parametersType = method.getParameterTypes();
    Object[] parameters = new Object[parametersType.length];
    for (int i = 0; i < parametersType.length; i++) {
      parameters[i] = parametersType[i].cast(null);
    }
    return parameters;
  }


}
