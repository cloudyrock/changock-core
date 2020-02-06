package io.changock.runner.base;


import io.changock.driver.api.driver.ChangeSetDependency;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class DependencyManagerTest {

  @Test
  public void shouldRetrieveConnectorDependency_WhenAddSimpleDependency() {
    Assert.assertEquals("dependency",
        new DependencyManager()
            .addConnectorDependency(new ChangeSetDependency("dependency"))
            .getDependency(String.class)
            .orElseThrow(RuntimeException::new));
  }

  @Test
  public void shouldRetrieveLastConnectorDependency_WhenOverride() {
    Assert.assertEquals("dependency",
        new DependencyManager()
            .addConnectorDependency(new ChangeSetDependency("dependencyNotReturned"))
            .addConnectorDependency(new ChangeSetDependency("dependency"))
            .getDependency(String.class)
            .orElseThrow(RuntimeException::new));
  }

  @Test
  public void shouldRetrieveLastConnectorDependency_WhenAddSet() {
    ArrayList<ChangeSetDependency> dependencies = new ArrayList();
    dependencies.add(new ChangeSetDependency(100L));
    dependencies.add(new ChangeSetDependency("dependency"));
    Assert.assertEquals("dependency",
        new DependencyManager()
            .addConnectorDependency(dependencies)
            .getDependency(String.class)
            .orElseThrow(RuntimeException::new));
  }

  @Test
  public void shouldRetrieveLastConnectorDependency_WhenAddSetAndSimple_IfOverride() {
    ArrayList<ChangeSetDependency> dependencies = new ArrayList();
    dependencies.add(new ChangeSetDependency(100L));
    dependencies.add(new ChangeSetDependency("dependencyNotReturned"));
    Assert.assertEquals("dependency",
        new DependencyManager()
            .addConnectorDependency(dependencies)
            .addConnectorDependency(new ChangeSetDependency("dependency"))
            .getDependency(String.class)
            .orElseThrow(RuntimeException::new));
  }


  @Test
  public void shouldRetrieveChildConnectorDependency_WhenAddChild_IfRetrievedParent() {

    Child1 dependency = new Child1();

    Assert.assertEquals(dependency,
        new DependencyManager()
            .addConnectorDependency(new ChangeSetDependency(Child1.class, dependency))
            .getDependency(Parent.class)
            .orElseThrow(RuntimeException::new));
  }

  @Test
  public void shouldRetrieveFirstChildConnectorDependency_WhenAddTwoChild_IfRetrievedParent() {
    Child1 dependency = new Child1();
    Assert.assertEquals(dependency,
        new DependencyManager()
            .addConnectorDependency(new ChangeSetDependency(Child1.class, dependency))
            .addConnectorDependency(new ChangeSetDependency(Child2.class, new Child2()))
            .getDependency(Parent.class)
            .orElseThrow(RuntimeException::new));
  }


  @Test
  public void shouldRetrieveStandardDependency_WhenAddSimpleDependency() {
    Assert.assertEquals("dependency",
        new DependencyManager()
            .addStandardDependency(new ChangeSetDependency("dependency"))
            .getDependency(String.class)
            .orElseThrow(RuntimeException::new));
  }

  @Test
  public void shouldRetrieveLastStandardDependency_WhenOverride() {
    Assert.assertEquals("dependency",
        new DependencyManager()
            .addStandardDependency(new ChangeSetDependency("dependencyNotReturned"))
            .addStandardDependency(new ChangeSetDependency("dependency"))
            .getDependency(String.class)
            .orElseThrow(RuntimeException::new));
  }

  @Test
  public void shouldRetrieveLastStandardDependency_WhenAddSet() {
    ArrayList<ChangeSetDependency> dependencies = new ArrayList();
    dependencies.add(new ChangeSetDependency(100L));
    dependencies.add(new ChangeSetDependency("dependency"));
    Assert.assertEquals("dependency",
        new DependencyManager()
            .addStandardDependency(dependencies)
            .getDependency(String.class)
            .orElseThrow(RuntimeException::new));
  }

  @Test
  public void shouldRetrieveLastStandardDependency_WhenAddSetAndSimple_IfOverride() {
    ArrayList<ChangeSetDependency> dependencies = new ArrayList<>();
    dependencies.add(new ChangeSetDependency(100L));
    dependencies.add(new ChangeSetDependency("dependencyNotReturned"));
    Assert.assertEquals("dependency",
        new DependencyManager()
            .addStandardDependency(dependencies)
            .addStandardDependency(new ChangeSetDependency("dependency"))
            .getDependency(String.class)
            .orElseThrow(RuntimeException::new));
  }


  @Test
  public void shouldRetrieveChildStandardDependency_WhenAddChild_IfRetrievedParent() {

    Child1 dependency = new Child1();

    Assert.assertEquals(dependency,
        new DependencyManager()
            .addStandardDependency(new ChangeSetDependency(Child1.class, dependency))
            .getDependency(Parent.class)
            .orElseThrow(RuntimeException::new));
  }

  @Test
  public void shouldRetrieveFirstChildCStandardDependency_WhenAddTwoChild_IfRetrievedParent() {
    Child1 dependency = new Child1();
    Assert.assertEquals(dependency,
        new DependencyManager()
            .addStandardDependency(new ChangeSetDependency(Child1.class, dependency))
            .addStandardDependency(new ChangeSetDependency(Child2.class, new Child2()))
            .getDependency(Parent.class)
            .orElseThrow(RuntimeException::new));
  }


  @Test
  public void shouldPrioritizeConnectorDependency() {
    Assert.assertEquals("connectorDependency",
        new DependencyManager()
            .addStandardDependency(new ChangeSetDependency("standardDependency"))
            .addConnectorDependency(new ChangeSetDependency("connectorDependency"))
            .addStandardDependency(new ChangeSetDependency("standardDependency"))
            .getDependency(String.class)
            .orElseThrow(RuntimeException::new));
  }


}

class Parent {
}

class Child1 extends Parent {
}

class Child2 extends Parent {
}
