package com.treefinance.crawler.framework.config.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since Feb 7, 2014 10:11:39 AM
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Node {

    String value() default "";

    boolean required() default false;

    Class<?>[] types() default {};

    boolean registered() default false;//whether register to content

    boolean referenced() default false;//ref in content
}
