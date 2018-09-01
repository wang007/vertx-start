package org.wang007.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * 标记型接口， 注入组件时，父类的属性是否需要注入
 *
 * 如果父类的属性使用了注入注解，需要被注入时， 请在父类中使用该注解
 *
 * 默认情况下， 不加{@link InjectToSuper}的话，是不会加载父类属性的
 *
 * <code>
 *
 *      @InjectToSuper
 *     public class Father {
 *
 *         @Value("person.fatherName")
 *         private String fatherName;
 *
 *         @Inject
 *         private Demo demo;
 *
 *         private Test test;
 *     }
 *
 *     @Component
 *     public class Son extends Father {
 *
 *         @Value("person.sonName")
 *         privat String sonName;
 *
 *     }
 *
 *     Father类中的 fatherName, demo能注入属性， 而test 不会
 *
 *
 *     如果Father类中去掉@InjectToSuper,  fatherName， demo 不会注入、
 *
 * </code>
 *
 * created by wang007 on 2018/8/26
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface InjectToSuper {

}
