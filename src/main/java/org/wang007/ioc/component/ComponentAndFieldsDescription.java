package org.wang007.ioc.component;

import org.wang007.utils.CollectionUtils;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 组件描述， 包括field的描述
 *
 *
 * 组件描述 必须是 immutable object
 *
 * created by wang007 on 2018/8/25
 */
public class ComponentAndFieldsDescription extends ComponentDescription {

    public final List<? extends InjectPropertyDescription> propertyDescriptions;

    private int hash;

    protected ComponentAndFieldsDescription(ComponentDescription cd, List<? extends InjectPropertyDescription> pd) {
        super(cd);
        this.propertyDescriptions = pd;
    }

    protected ComponentAndFieldsDescription(Class<?> clazz, List<Class<?>> superClasses,
                                         boolean isVertilce, boolean isLoadRouter, Annotation annotation,
                                         String componentName, boolean isSingle, List<Annotation> otherAnnotations,
                                         List<? extends InjectPropertyDescription> propertyDescriptions) {
        super(clazz, superClasses, isVertilce, isLoadRouter, annotation, componentName, isSingle, otherAnnotations);
        this.propertyDescriptions = propertyDescriptions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ComponentAndFieldsDescription that = (ComponentAndFieldsDescription) o;
        return Objects.equals(propertyDescriptions, that.propertyDescriptions);
    }

    @Override
    public int hashCode() {
        int h = hash;
        if(h == 0) {
            h = Objects.hash(super.hashCode(), propertyDescriptions);
            hash = h;
        }
        return h;
    }

    //=========================== builder =====================================


    public static class Builder extends ComponentDescription.Builder {

        private List<? extends InjectPropertyDescription> propertyDescriptions;

        public static Builder builder() {
            return new Builder();
        }

        public static Builder builder(ComponentDescription cd) {
            Builder builder = builder();
            builder.clazz = cd.clazz;
            builder.isVertilce = cd.isVertilce;
            builder.isLoadRouter = cd.isLoadRouter;
            builder.annotation = cd.annotation;
            builder.componentName = cd.componentName;
            builder.isSingle = cd.isSingle;
            builder.otherAnnotations = cd.otherAnnotations;
            builder.superClasses = cd.superClasses;

            return builder;
        }

        public Builder propertyDescriptions(List<? extends InjectPropertyDescription> pd) {
            if(CollectionUtils.isEmpty(pd)) propertyDescriptions = Collections.emptyList();
            else propertyDescriptions = Collections.unmodifiableList(pd);
            return this;
        }

        public ComponentAndFieldsDescription build() {
            return new ComponentAndFieldsDescription(clazz, superClasses, isVertilce,
                    isLoadRouter, annotation, componentName,
                    isSingle, otherAnnotations, propertyDescriptions);
        }

    }



}
