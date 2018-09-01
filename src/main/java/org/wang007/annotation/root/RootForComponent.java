package org.wang007.annotation.root;

import org.wang007.annotation.Deploy;
import org.wang007.annotation.Route;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  元注解   用于标识自定义注解  例如： {@link Route}, {@link Deploy}等
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface RootForComponent {}
