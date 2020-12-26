package io.changock.runner.spring.v5.profiles.enseuredecorators;


import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import io.changock.runner.spring.v5.util.CallVerifier;
import io.changock.runner.spring.v5.util.TemplateForTestImpl;
import io.changock.runner.spring.v5.util.TemplateForTestImplChild;


@ChangeLog(order = "01")
public class EnsureDecoratorChangerLog {

    @ChangeSet(author = "testuser", id = "ensureDecoratorChangeSet", order = "01")
    public void ensureDecoratorChangeSet(
            CallVerifier callVerifier,
            TemplateForTestImpl templateForTest) {
        callVerifier.counter++;
        System.out.println("invoked ensureDecoratorChangeSet");

        if (templateForTest == null) {
            throw new RuntimeException("Must pass dependency");
        }
        if (!TemplateForTestImplChild.class.isAssignableFrom(templateForTest.getClass())) {
            throw new RuntimeException("Must prioritise pass connector dependency");
        }

    }

}
