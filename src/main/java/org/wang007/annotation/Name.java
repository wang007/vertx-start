package org.wang007.annotation;

import io.vertx.core.Vertx;
import org.wang007.ioc.ComponentDefinition;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于{@link ComponentDefinition#supplyComponent(Vertx)}, 用于给生成的组件提供别名
 *
 * 默认是首字母小写的类名
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Name {

    /**
     * 定义组件的别名
     */
    String value() default "";


}
