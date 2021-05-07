package com.github.cloudyrock.springboot.v2_4.profiles.enseuredecorators;


import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.github.cloudyrock.springboot.v2_2.util.CallVerifier;
import com.github.cloudyrock.springboot.v2_2.util.TemplateForTestImpl;
import com.github.cloudyrock.springboot.v2_2.util.TemplateForTestImplChild;


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
