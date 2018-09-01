package org.wang007.annotation;

import org.wang007.annotation.root.RootForComponent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 被该注解的class, 可以注入到其他的组件中。
 *
 * <code>
 *     @Component
 *     public class Demo {
 *
 *         @Inject  //byType注入
 *         private Test test;
 *
 *         @Inject("human") //byName 注入
 *         private Person person;
 *
 *         @Value("grilFriend")
 *         private String grilFriend;
 *     }
 *
 * </code>
 *
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@RootForComponent
public @interface Component {

    /**
     * 定义组件名 默认是首字母小写的类名
     *
     */
     String value() default "";
}
