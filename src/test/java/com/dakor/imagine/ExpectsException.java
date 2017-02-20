package com.dakor.imagine;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotation to set a type and a message of the expected exception while throwing the one by testing feature.
 *
 * @author dkor
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface ExpectsException {
    Class<? extends Throwable> type();

    String message() default "";
}
