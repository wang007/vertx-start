package org.wang007.annotation;

import org.wang007.annotation.root.RootForComponent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 属性集合的注解
 *
 * <code>
 *     配置文件属性：  mysql.datasource.username=root
 *                  mysql.datasource.password=root
 *                  mysql.datasource.driverClassName=com.mysql.jdbc.Driver
 *                  mysql.datasource.poolSize=8
 *                  mysql.datasource.shabi=woshishabi
 *
 *     @ConfigurationProperties(prefix = "mysql.datasource")
 *     public class DataBaseProperty {
 *
 *          private String username;
 *
 *          private String password;
 *
 *          private String driverClassName;
 *
 *          private int poolSize;
 *
 *          @Value("shabi")
 *          private String foolish;
 *
 *          private String
 *
 *          //getter and setter
 *     }
 *
 * </code>
 *
 *
 * Created by wang007 on 2018/8/24.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@RootForComponent
public @interface ConfigurationProperties {

    /**
     * 定义组件名 默认是首字母小写的类名
     */
    String value() default "";


    /**
     * 属性前缀。
     */
    String prefix() default "";

}
