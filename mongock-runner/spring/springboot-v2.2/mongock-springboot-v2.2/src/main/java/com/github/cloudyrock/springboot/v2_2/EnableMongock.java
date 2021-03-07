package com.github.cloudyrock.springboot.v2_2;

import com.github.cloudyrock.springboot.v2_2.config.MongockContext;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({MongockContext.class})
public @interface EnableMongock {
}
