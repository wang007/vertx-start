package org.wang007.annotation;

import org.wang007.annotation.root.RootForInject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注入，使用该注解就能把组件注入到属性中
 *
 * 注意：只能通过属性注入
 *
 * 注意：vertx-base定义的组件都是单利的， 用户自己保证线程安全
 *
 * <code>
 *
 *     @Component
 *     public class Demo {
 *
 *         @Inject  //byType 注入
 *         private Test test;
 *
 *         @Inject //byName 注入
 *         private Person zhangsan;
 *
 *     }
 *
 * </code>
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@RootForInject
public @interface Inject {

    /**
     * 注入时， 根据组件名注入
     * 默认根据类型注入
     */
    String value() default "";
}
