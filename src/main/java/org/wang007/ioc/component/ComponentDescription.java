package org.wang007.ioc.component;

import org.wang007.annotation.root.RootForComponent;
import org.wang007.utils.CollectionUtils;

import java.lang.annotation.Annotation;
import java.util.*;

/**
 * 组件描述 被{@link RootForComponent}元注解的子注解 注解的class 的描述。
 *
 * 注意： 组件描述是 immutable object
 *
 * created by wang007 on 2018/8/23
 */
public class ComponentDescription {

    /**
     * class
     */
    public final Class<?> clazz;

    /**
     * class是否是Verticle
     */
    public final boolean isVertilce;

    /**
     * class是否是LoadRouter
     */
    public final boolean isLoadRouter;

    /**
     * 被{@link RootForComponent}注解的注解， 即注解到这个class上
     */
    public final Annotation annotation;

    /**
     * 组件名
     * 默认值：首字母小写的类名
     * 定义了类名的话， 那就是类型名了。
     */
    public final String componentName;

    /**
     * 是否是单例的
     * true: 单例
     * false: 多例
     */
    public final boolean isSingle;

    /**
     * 除注解组件的之外的其他注解
     *
     * 拓展属性，暂时没用上
     */
    public final List<Annotation> otherAnnotations;

    /**
     * 所有的父类
     */
    public final List<Class<?>> superClasses;

    private int hash ;

    protected ComponentDescription(Class<?> clazz, List<Class<?>> superClasses, boolean isVertilce,
                                boolean isLoadRouter, Annotation annotation, String componentName) {
        this.clazz = clazz;
        this.superClasses = superClasses;
        this.isVertilce = isVertilce;
        this.isLoadRouter = isLoadRouter;
        this.annotation = annotation;
        this.componentName = componentName;
        this.isSingle = false;
        otherAnnotations = Collections.emptyList();

    }

    protected ComponentDescription(Class<?> clazz, List<Class<?>> superClasses, boolean isVertilce, boolean isLoadRouter,
                                Annotation annotation, String componentName, boolean isSingle, List<Annotation> otherAnnotations) {
        this.clazz = clazz;
        this.superClasses = superClasses;
        this.isVertilce = isVertilce;
        this.isLoadRouter = isLoadRouter;
        this.annotation = annotation;
        this.componentName = componentName;
        this.isSingle = isSingle;
        this.otherAnnotations = otherAnnotations;
    }

    protected ComponentDescription(ComponentDescription cd) {
        this.clazz = cd.clazz;
        this.superClasses = cd.superClasses;
        this.isVertilce = cd.isVertilce;
        this.isLoadRouter = cd.isLoadRouter;
        this.annotation = cd.annotation;
        this.componentName = cd.componentName;
        this.isSingle = cd.isSingle;
        this.otherAnnotations = cd.otherAnnotations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComponentDescription that = (ComponentDescription) o;
        return isVertilce == that.isVertilce &&
                isLoadRouter == that.isLoadRouter &&
                isSingle == that.isSingle &&
                Objects.equals(clazz, that.clazz) &&
                Objects.equals(annotation, that.annotation) &&
                Objects.equals(componentName, that.componentName) &&
                Objects.equals(otherAnnotations, that.otherAnnotations) &&
                Objects.equals(superClasses, that.superClasses);
    }

    @Override
    public int hashCode() {
        int h = hash;
        if(h ==0) {
            h = Objects.hash(clazz, isVertilce, isLoadRouter, annotation,
                    componentName, isSingle, otherAnnotations, superClasses);
            hash = h;
        }
        return h;
    }


    //=============================== builder ======================================================


    public static class Builder {

        protected   Class<?> clazz;


        protected  boolean isVertilce;


        protected  boolean isLoadRouter;


        protected  Annotation annotation;


        protected  String componentName;


        protected  boolean isSingle;


        protected List<Annotation> otherAnnotations = Collections.emptyList();


        protected List<Class<?>> superClasses = Collections.emptyList();

        public static Builder builder() {
            return new Builder();
        }

        public Builder clazz(Class<?> clazz) {
            this.clazz = clazz;
            return this;
        }

        public Builder isVertilce(boolean isVertilce) {
            this.isVertilce = isVertilce;
            return this;
        }

        public Builder isLoadRouter(boolean isLoadRouter) {
            this.isLoadRouter = isLoadRouter;
            return this;
        }

        public Builder annotation(Annotation ann) {
            this.annotation = ann;
            return this;
        }

        public Builder componentName(String componentName) {
            this.componentName = componentName;
            return this;
        }

        public Builder isSingle(boolean isSingle) {
            this.isSingle = isSingle;
            return this;
        }

        public Builder otherAnnotations(List<Annotation> otherAnns) {
            if(CollectionUtils.isEmpty(otherAnnotations)) {
                this.otherAnnotations = Collections.emptyList();
            } else {
                this.otherAnnotations = Collections.unmodifiableList(otherAnnotations);
            }
            this.otherAnnotations = otherAnns;
            return this;
        }

        public Builder superClasses(List<Class<?>> superClasses) {
            if(CollectionUtils.isEmpty(superClasses)) {
                this.superClasses = Collections.emptyList();
            } else {
                this.superClasses = Collections.unmodifiableList(superClasses);
            }
            return this;
        }

        public ComponentDescription build() {
            return new ComponentDescription(clazz, superClasses, isVertilce,
                    isLoadRouter, annotation, componentName, isSingle, otherAnnotations);

        }
    }


}
