package io.changock.test.util.decorator;

import io.changock.driver.api.lock.LockManager;
import io.changock.driver.api.lock.guard.invoker.LockGuardInvoker;
import io.changock.driver.api.lock.guard.invoker.LockGuardInvokerImpl;
import io.changock.migration.api.annotations.NonLockGuarded;
import io.changock.migration.api.annotations.NonLockGuardedType;
import org.mockito.Mockito;
import org.mockito.invocation.Invocation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;


//TODO make this validate all the interfaces coming from a set of sources
// for example from MongockTemplate, check all the methods(obviously) but also check all the returningTypes.
// any method which returningType shouldn't be validated, should be annotated with @NotDecoratorReturn
// any method that should nt be validated at allo, should be annotated with @NotDecorated
// alternatively to this approach, a set of methods could be added to the DecoratorDefinition. This approach
// it's less intrusive
public class DecoratorValidator {

  private final Collection<Class> typeDecorators;
  private final Map<Class, Object> instancesMap;
  private final LockManager lockManager;
  private final DecoratorTestCollection trackedDecorators;
  private final Collection<Class> ignoredTypes;
  private final boolean ignorePrimitives;
  private DecoratorTestCollection decoratorsNextToProcess;

  public DecoratorValidator(DecoratorTestCollection decorators,
                            Collection<Class> ignoredTypes,
                            Collection<Class> typeDecorators,
                            Map<Class, Object> instancesMap,
                            LockManager lockManager) {
    this(decorators, ignoredTypes, typeDecorators, instancesMap, true, lockManager);
  }

  public DecoratorValidator(DecoratorTestCollection decorators,
                            Collection<Class> ignoredTypes,
                            Collection<Class> typeDecorators,
                            Map<Class, Object> instancesMap,
                            boolean ignorePrimitives,
                            LockManager lockManager) {
    decoratorsNextToProcess = decorators;
    this.ignoredTypes = ignoredTypes;
    this.typeDecorators = typeDecorators;
    this.instancesMap = instancesMap;
    this.ignorePrimitives = ignorePrimitives;
    this.lockManager = lockManager;
    trackedDecorators = new DecoratorTestCollection();
  }


  public List<DecoratorMethodFailure> checkAndReturnFailedDecorators() {
    List<DecoratorMethodFailure> result = new ArrayList<>();
    while (decoratorsNextToProcess.size() > 0) {
      DecoratorTestCollection decoratorsToProcess = new DecoratorTestCollection(decoratorsNextToProcess);
      trackedDecorators.addAll(decoratorsToProcess);
      decoratorsNextToProcess = new DecoratorTestCollection();
      List<DecoratorMethodFailure> partialResult = decoratorsToProcess
          .stream()
          .map(decoratorDefinition -> getMethodErrorsFromDecorator(decoratorDefinition, decoratorDefinition.getInterfaceType()))
          .flatMap(Collection::stream)
          .collect(Collectors.toList());
      result.addAll(partialResult);
    }
    return result;
  }


  private Collection<DecoratorMethodFailure> getMethodErrorsFromDecorator(DecoratorDefinition decorator, Class interfaceType) {
    Method[] declaredMethods = interfaceType.getDeclaredMethods();
    Collection<DecoratorMethodFailure> methodFailures = Stream.of(declaredMethods)
        .map(method -> getMethodErrorOptional(method, decorator))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.toList());

    methodFailures.addAll(getDecoratorMethodFailuresFromParentInterfaces(decorator, interfaceType));
    return methodFailures;
  }

  private Collection<DecoratorMethodFailure> getDecoratorMethodFailuresFromParentInterfaces(DecoratorDefinition decorator, Class interfaceType) {
    return Stream.of(interfaceType.getInterfaces())
        .map(type -> getMethodErrorsFromDecorator(decorator, type))
        .flatMap(Collection::stream)
        .collect(Collectors.toList());
  }

  @SuppressWarnings("unchecked")
  private Optional<DecoratorMethodFailure> getMethodErrorOptional(Method interfaceMethod, DecoratorDefinition decorator) {
    Method method;
    try {
      method = decorator.getImplementingType().getMethod(interfaceMethod.getName(), interfaceMethod.getParameterTypes());
      method.setAccessible(true);
      List<NonLockGuardedType> noGuardedLockTypes = getNonLockGuardedTypes(method);
      if(noGuardedLockTypes.contains(NonLockGuardedType.NONE)) {
        return Optional.empty();
      }
      Object result = executeMethod(method, decorator);
      boolean errorEnsuringLock = isErrorEnsuringLock(noGuardedLockTypes, decorator);
      addResultToValidateIfRequired(result, method);
      boolean errorReturningDecorator = isErrorReturningDecorator(method, result, decorator);
      String otherErrorDetail = shouldReturnObjectBeGuarded(method)
          && result == null
          ? "returns null"
          : "";
      return errorEnsuringLock || errorReturningDecorator
          ? Optional.of(new DecoratorMethodFailure(decorator.getImplementingType(), method, errorReturningDecorator, errorEnsuringLock, otherErrorDetail))
          : Optional.empty();
    } catch (Exception ex) {
      return Optional.of(DecoratorMethodFailure.otherError(decorator.getImplementingType(), interfaceMethod, ex.getMessage() != null ? ex.getMessage() : ex.getClass().getSimpleName()));
    }
  }

  private List<NonLockGuardedType> getNonLockGuardedTypes(Method method) {
    NonLockGuarded nonLockGuarded = method.getAnnotation(NonLockGuarded.class);
    return nonLockGuarded != null ? Arrays.asList(nonLockGuarded.value()) : Collections.emptyList();
  }

  private void addResultToValidateIfRequired(Object result, Method method) {
    if (shouldNotBeIgnored(method)
        && result != null //if null, it will throw an NullPointerException
        && !trackedDecorators.contains(method.getReturnType(), result.getClass())
        && !decoratorsNextToProcess.contains(method.getReturnType(), result.getClass())
        && shouldReturnObjectBeGuarded(method)) {
      decoratorsNextToProcess.addRawDecorator(method.getReturnType(), result.getClass());
    }
  }

  private boolean isErrorEnsuringLock(List<NonLockGuardedType> noGuardedLockTypes, DecoratorDefinition decorator) throws NoSuchMethodException {
    if (noGuardedLockTypes.contains(NonLockGuardedType.METHOD) || noGuardedLockTypes.contains(NonLockGuardedType.NONE)) {
      return false;
    }
    Collection<Invocation> invocations = Mockito.mockingDetails(lockManager).getInvocations();
    return (invocations.size() != 1 || !invocations.iterator()
        .next()
        .getMethod()
        .equals(LockManager.class.getMethod("ensureLockDefault")));
  }

  private boolean isErrorReturningDecorator(Method method, Object result, DecoratorDefinition decorator) throws Exception {
    Mockito.reset(lockManager);
    return shouldReturnObjectBeGuarded(method) && (result == null || !isDecoratorImplementation(result));

  }

  private Object executeMethod(Method method, DecoratorDefinition decorator) throws IllegalAccessException, InvocationTargetException {
    Object instance = instancesMap.containsKey(decorator.getImplementingType())
        ? instancesMap.get(decorator.getImplementingType())
        : getDecoratorInstance(decorator, new LockGuardInvokerImpl(lockManager));
    return method.invoke(instance, getDefaultParametersFromMethod(method));
  }


  @SuppressWarnings("unchecked")
  private Object getDecoratorInstance(DecoratorDefinition decorator, LockGuardInvoker invokerMock) {
    try {
      return decorator.getImplementingType()
          .getConstructor(decorator.getInterfaceType(), LockGuardInvoker.class)
          .newInstance(Mockito.mock(decorator.getImplementingType()), invokerMock);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  //TODO improve the way to validate if it's validator
  private boolean isDecoratorImplementation(Object result) {
    Class<?> resultClass = result.getClass();
    return typeDecorators.contains(resultClass) || resultClass.getSimpleName().endsWith("DecoratorImpl");
  }

  private static Object[] getDefaultParametersFromMethod(Method method) {
    Class[] parametersType = method.getParameterTypes();
    Object[] parameters = new Object[parametersType.length];
    for (int i = 0; i < parametersType.length; i++) {
      if (parametersType[i].isPrimitive()) {
        parameters[i] = getDefaultPrimitiveValue(parametersType[i]);
      } else {
        parameters[i] = parametersType[i].cast(null);

      }
    }
    return parameters;
  }

  private static Object getDefaultPrimitiveValue(Class clazz) {
    if (clazz == Integer.TYPE)
      return Integer.MIN_VALUE;

    if (clazz == Long.TYPE)
      return Long.MIN_VALUE;

    if (clazz == Boolean.TYPE)
      return false;

    if (clazz == Byte.TYPE)
      return Byte.MIN_VALUE;

    if (clazz == Character.TYPE)
      return 'c';

    if (clazz == Float.TYPE)
      return Float.MIN_VALUE;

    if (clazz == Double.TYPE)
      return Double.MIN_VALUE;

    if (clazz == Short.TYPE)
      return Short.MIN_VALUE;

    return null;
  }

  private boolean shouldReturnObjectBeGuarded(Method method) {
    List<NonLockGuardedType> noGuardedLockTypes = getNonLockGuardedTypes(method);

    return !Void.TYPE.equals(method.getReturnType())
        && shouldNotBeIgnored(method)
        && !noGuardedLockTypes.contains(NonLockGuardedType.RETURN)
        && !noGuardedLockTypes.contains(NonLockGuardedType.NONE)
        && !method.getReturnType().isAnnotationPresent(NonLockGuarded.class);
  }


  private boolean shouldNotBeIgnored(Method method) {
    Class<?> returnType = method.getReturnType();
    return !(isPrimitiveIgnored(returnType) || ignoredTypes.contains(returnType));
  }

  private boolean isPrimitiveIgnored(Class c) {
    return ignorePrimitives && (c.isPrimitive() || String.class.equals(c));
  }


}