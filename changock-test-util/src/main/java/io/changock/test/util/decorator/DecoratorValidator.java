package io.changock.test.util.decorator;

import io.changock.driver.api.lock.LockManager;
import io.changock.driver.core.lock.guard.invoker.LockGuardInvoker;
import io.changock.driver.core.lock.guard.invoker.LockGuardInvokerImpl;
import org.mockito.Mockito;
import org.mockito.invocation.Invocation;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
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

  private LockManager lockManager;

  private final DecoratorTestCollection decorators;

  public DecoratorValidator(DecoratorTestCollection decorators, LockManager lockManager) {
    this.decorators = decorators;
    this.lockManager = lockManager;

  }

  public List<DecoratorMethodFailure> checkAndReturnFailedDecorators() {
    return decorators.values()
        .stream()
        .map(decoratorDefinition -> getMethodErrorsFromDecorator(decoratorDefinition, decoratorDefinition.getInterfaceType()))
        .flatMap(Collection::stream)
        .collect(Collectors.toList());
  }


  private Collection<DecoratorMethodFailure> getMethodErrorsFromDecorator(DecoratorDefinition decorator, Class interfaceType) {
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

  private Optional<DecoratorMethodFailure> getMethodErrorOptional(Method method, DecoratorDefinition decorator) {
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

  private boolean isErrorEnsuringLock(Method method, DecoratorDefinition decorator) throws NoSuchMethodException {

    Collection<Invocation> invocations = Mockito.mockingDetails(lockManager).getInvocations();
    return !decorator.getNoLockGardMethods().contains(method.getName()) && (invocations.size() != 1 || !invocations.iterator()
        .next()
        .getMethod()
        .equals(LockManager.class.getMethod("ensureLockDefault")));
  }

  private boolean isErrorReturningDecorator(Method method, DecoratorDefinition decorator, Class<?> returnType) throws Exception {
    Mockito.reset(lockManager);
    //method.invoke needs to be executed
    Object result = method.invoke(
        decorator.getInstance().orElseGet(() -> getDecoratorInstance(decorator, new LockGuardInvokerImpl(lockManager))),
        getNullParametersFromMethod(method));
    return !Void.TYPE.equals(returnType) && decorators.containsKey(returnType) && !isDecoratorImplementation(result);
  }


  @SuppressWarnings("unchecked")
  private Object getDecoratorInstance(DecoratorDefinition decorator, LockGuardInvoker invokerMock) {
    try {
      Object instance = decorator.getImplementingType()
          .getConstructor(decorator.getInterfaceType(), LockGuardInvoker.class)
          .newInstance(Mockito.mock(decorator.getImplementingType()), invokerMock);
      return instance;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private boolean isDecoratorImplementation(Object result) {
    return decorators.values().stream().map(DecoratorDefinition::getImplementingType).anyMatch(result.getClass()::equals);
  }

  private static Object[] getNullParametersFromMethod(Method method) {
    Class[] parametersType = method.getParameterTypes();
    Object[] parameters = new Object[parametersType.length];
    for (int i = 0; i < parametersType.length; i++) {
      parameters[i] = parametersType[i].cast(null);
    }
    return parameters;
  }


}
