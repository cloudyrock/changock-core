package io.changock.runner.spring.v5.profiles.enseuredecorators;


import io.changock.migration.api.annotations.ChangeLog;
import io.changock.migration.api.annotations.ChangeSet;
import io.changock.runner.spring.v5.util.CallVerifier;
import io.changock.runner.spring.v5.util.MongockTemplateForTestImpl;
import io.changock.runner.spring.v5.util.MongockTemplateForTestImplChild;


@ChangeLog(order = "01")
public class EnsureDecoratorChangerLog {

    @ChangeSet(author = "testuser", id = "ensureDecoratorChangeSet", order = "01")
    public void ensureDecoratorChangeSet(
            CallVerifier callVerifier,
            MongockTemplateForTestImpl mongockTemplateForTest) {
        callVerifier.counter++;
        System.out.println("invoked ensureDecoratorChangeSet");

        if (mongockTemplateForTest == null) {
            throw new RuntimeException("Must pass dependency");
        }
        if (!MongockTemplateForTestImplChild.class.isAssignableFrom(mongockTemplateForTest.getClass())) {
            throw new RuntimeException("Must prioritise pass connector dependency");
        }

    }

}
