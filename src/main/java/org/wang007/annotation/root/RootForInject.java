package org.wang007.annotation.root;

import org.wang007.annotation.Inject;
import org.wang007.annotation.Value;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记型注解  用于标记{@link Inject} 和 {@link Value}等注入型注解
 *
 * Created by wang007 on 2018/8/24.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface RootForInject {
}
