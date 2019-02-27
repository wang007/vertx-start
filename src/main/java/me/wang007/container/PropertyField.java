package me.wang007.container;


import me.wang007.utils.CollectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

/**
 * 属性组件定义。
 *
 * 属性组件构成：
 *  1. 属性名。
 *  2. 属性field
 *  3. 属性类型
 *  4. 属性注解
 *
 * created by wang007 on 2019/2/25
 */
public class PropertyField {

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
     * 属性注解
     */
    public final List<Annotation> annotations;


    protected PropertyField(String fieldName, Field field, Class<?> fieldClass, List<Annotation> annotations) {
        this.fieldName = fieldName;
        this.field = field;
        this.fieldClass = fieldClass;
        this.annotations = annotations;
    }

    public static class Builder {

        private String fieldName;

        private Field field;

        private Class<?> fieldClass;

        private List<Annotation> annotations = Collections.emptyList();

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

        public Builder annotations(List<Annotation> annotations) {
            if(CollectionUtils.isEmpty(annotations)) {
                this.annotations = Collections.emptyList();
            } else  {
                this.annotations = Collections.unmodifiableList(annotations);
            }
            return this;
        }

        public PropertyField build() {
            return new PropertyField(fieldName, field, fieldClass, annotations);
        }

    }



}
