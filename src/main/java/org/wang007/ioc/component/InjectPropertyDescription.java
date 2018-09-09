package org.wang007.ioc.component;

import org.wang007.annotation.Inject;
import org.wang007.annotation.Value;
import org.wang007.annotation.root.RootForInject;
import org.wang007.utils.CollectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

/**
 * {@link RootForInject}元注解的子注解 描述的属性
 *
 * 注意：注入属性描述是 immutable object
 *
 * Created by wang007 on 2018/8/24.
 */
public class InjectPropertyDescription {


    /**
     * 属性名
     */
    public final String fieldName;

    /**
     * 属性
     */
    public final Field field;


    /**
     * 属性的class
     */
    public final Class<?> fieldClass;

    /**
     * 是否是byType注入   属性注入时，只能是byName注入 即 always false
     *
     * true: byType注入
     * false: byName注入
     */
    public final boolean byTypeInject;

    /**
     * 注入时的名称  如果没有设置的话就是 ""
     *
     * byName注入时，根据此名称注入
     *
     * 目前只有{@link Inject}和 {@link Value}两个注解中的值
     *
     */
    public final String injectKeyName;

    /**
     * 注入有两种情况，
     * 1. 值注入， 从配置文件中读取
     * 2. 组件注入
     *
     * true:  组件注入
     * false: 属性注入
     *
     */
    public final boolean componentInject;

    /**
     * 注入组件的注解  例如 {@link Inject}，{@link Value} 等
     *
     * 注意：annotation 可能为空
     */
    public final Annotation annotation;


    /**
     *  除注入组件的注解， 其他注解
     */
    public final List<Annotation> otherAnnotations;


    protected InjectPropertyDescription(String fieldName, Field field, Class<?> fieldClass, boolean byTypeInject,
                                     String injectKeyName, boolean componentInject, Annotation annotation) {
        this.fieldName = fieldName;
        this.field = field;
        this.fieldClass = fieldClass;
        this.byTypeInject = byTypeInject;
        this.injectKeyName = injectKeyName;
        this.componentInject = componentInject;
        this.annotation = annotation;
        this.otherAnnotations = Collections.emptyList();
    }

    protected InjectPropertyDescription(String fieldName, Field field, Class<?> fieldClass, boolean byTypeInject,
                                     String injectKeyName, boolean componentInject, Annotation annotation, List<Annotation> otherAnnotations) {
        this.fieldName = fieldName;
        this.field = field;
        this.fieldClass = fieldClass;
        this.byTypeInject = byTypeInject;
        this.injectKeyName = injectKeyName;
        this.componentInject = componentInject;
        this.annotation = annotation;
        this.otherAnnotations = otherAnnotations;
    }


    //================================== builder ======================================


    public static class Builder {

        private String fieldName;

        private Field field;

        private Class<?> fieldClass;

        private boolean byTypeInject;

        private String injectKeyName;

        private boolean componentInject;

        private Annotation annotation;

        private List<Annotation> otherAnnotations;

        protected Builder(){}

        public static Builder builder() {
            return new Builder();
        }


        public Builder fieldName(String fieldName) {
            this.fieldName = fieldName;
            return this;
        }

        public Builder field(Field field) {
            this.field = field;
            return this;
        }


        public Builder fieldClass(Class<?> fieldClass) {
            this.fieldClass = fieldClass;
            return this;
        }


        public Builder byTypeInject(boolean byTypeInject) {
            this.byTypeInject = byTypeInject;
            return this;
        }


        public Builder injectKeyName(String injectKeyName) {
            this.injectKeyName = injectKeyName;
            return this;
        }


        public Builder componentInject(boolean componentInject) {
            this.componentInject = componentInject;
            return this;
        }


        public Builder annotation(Annotation annotation) {
            this.annotation = annotation ;
            return this;
        }


        public Builder otherAnnotations(List<Annotation> otherAnnotations) {
            if(CollectionUtils.isEmpty(otherAnnotations)) {
                this.otherAnnotations = Collections.emptyList();
            } else  {
                this.otherAnnotations = Collections.unmodifiableList(otherAnnotations);
            }
            return this;
        }

        public InjectPropertyDescription build() {
            return new InjectPropertyDescription(fieldName, field, fieldClass,
                    byTypeInject, injectKeyName, componentInject, annotation, otherAnnotations);
        }





    }

}
