package org.wang007.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 生成单例，多例的策略
 * 默认是单例的
 *
 * //TODO 未实现。 1. 引入多例，开发难度大增。 2. 绝大多数场景不需要这个。
 *
 * Created by wang007 on 2018/8/25.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Scope {

    /**
     * 默认的策略是单例的
     */
    Policy scopePolicy() default Policy.Single;

    /**
     * 策略
     */
    enum Policy {
         Single, //单例
         Prototype //多例
    }

}
