package com.github.cloudyrock.springboot.v2_2.config.test;

import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({MongockTestContext.class})
@Deprecated//since=4.3.6, forRemoval=true
public @interface MongockTestConfiguration {
}
