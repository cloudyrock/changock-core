package io.cloudyrock.changock.runner.spring.v5.profiles.enseuredecorators;


import io.changock.migration.api.annotations.ChangeLog;
import io.changock.migration.api.annotations.ChangeSet;
import io.cloudyrock.changock.runner.spring.v5.util.CallVerifier;
import io.cloudyrock.changock.runner.spring.v5.util.MongoTemplateForTest;
import io.cloudyrock.changock.runner.spring.v5.util.MongoTemplateForTestChild;


@ChangeLog(order = "01")
public class EnsureDecoratorChangerLog {

    @ChangeSet(author = "testuser", id = "ensureDecoratorChangeSet", order = "01")
    public void ensureDecoratorChangeSet(
            CallVerifier callVerifier,
            MongoTemplateForTest mongoTemplateForTest) {
        callVerifier.counter++;
        System.out.println("invoked ensureDecoratorChangeSet");

        if (mongoTemplateForTest == null) {
            throw new RuntimeException("Must pass dependency");
        }
        if (!MongoTemplateForTestChild.class.isAssignableFrom(mongoTemplateForTest.getClass())) {
            throw new RuntimeException("Must prioritise pass connector dependency");
        }

    }

}
