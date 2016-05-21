package fr.kissy.mapper.ghost.annotation;

import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Guillaume Le Biller (<i>guillaume.lebiller@gmail.com</i>)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.FIELD})
public @interface CssSelector {
    /**
     * Css selector value.
     */
    String value() default StringUtils.EMPTY;

    /**
     * If not empty, will select the given node attribute.
     */
    String attribute() default StringUtils.EMPTY;

    /**
     * If set to true, will throw an exception if the value is null or blank.
     */
    boolean required() default true;
}
