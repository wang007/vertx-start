package me.wang007.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注入组件属性的值注解
 *
 * 只能通过属性注入
 *
 * <code>
 *     如果配置文件里属性为 person.name, person.age
 *
 *     \@Value("person.name")
 *     private String name ;
 *
 *     \@Value("person.age")
 *     private int age;
 * </code>
 *
 * Created by wang007 on 2018/8/24.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Value {

    /**
     * @return 注入的属性的属性名
     */
    String value() default "";
}
