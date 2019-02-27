package me.wang007.container;

import me.wang007.utils.CollectionUtils;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * 组件定义
 * 描述组件构成：
 * 1.类注解
 * 2.类类型
 * 3.属性组件{@link PropertyField}
 * 4.父类组件
 *
 * 不包括 组件方法定义、
 *
 * created by wang007 on 2019/2/25
 */
public class Component {

    /**
     * class
     */
    public final Class<?> clazz;

    /**
     * 类上的注解
     */
    public final List<Annotation> annotations;

    /**
     * 组件名
     * 默认值：首字母小写的类名
     * 定义了类名的话， 那就是类型名了。
     */
    public final String componentName;


    /**
     * 类中属性定义
     *
     */
    public final List<PropertyField> propertyFields;


    /**
     * 父类组件， 包括接口
     *
     */
    public final List<Component> superComponents;

    /**
     * 是否被指定的注解 注解
     *
     * @param aClass 注解
     * @return true: 是， false：不是
     */
    public boolean annotationBy(Class<? extends Annotation> aClass) {
        for (Annotation an : annotations) {
            if(aClass.isAssignableFrom(an.getClass())) return true;
        }
        return false;
    }


    /**
     * 返回所有的属性，包括父类的
     * @return
     */
    public List<PropertyField> getAllPropertis() {
        List<PropertyField> list = new ArrayList<>(propertyFields);
        superComponents.forEach(sc -> {
            if(sc.propertyFields.size() != 0) list.addAll(sc.propertyFields);
        });
        return list;
    }



    /**
     * 获取指定的注解
     *
     * @param clz 目标注解类型
     * @return 目标注解 or null
     */
    public <E extends Annotation> E getAnnotation(Class<E> clz) {
        for (Annotation an: annotations) {
            if(clz.isAssignableFrom(an.getClass())) return (E) an;
        }
        return null;
    }


    /**
     * 是否 为该类的子类
     *
     * @param superFrom 父类
     * @return
     */
    public boolean superFrom(Class<?> superFrom) {
        for (Component superComponent : superComponents) {
            if(superComponent.clazz.equals(superFrom)) return true;
        }
        return false;
    }


    @Override
    public int hashCode() {
        return clazz.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if(obj instanceof Component) {
            Component c = (Component) obj;
            return clazz.equals(c.clazz);
        }
        return false;
    }

    protected Component(Class<?> clazz, List<Annotation> annotations, String componentName,
                        List<PropertyField> propertyFields, List<Component> superComponents) {
        this.clazz = clazz;
        this.annotations = annotations;
        this.componentName = componentName;
        this.propertyFields = propertyFields;
        this.superComponents = superComponents;
    }


    public static class Builder {

        private Class<?> clazz;

        private List<Annotation> annotations = Collections.emptyList();

        private String componentName;

        private List<PropertyField> propertyFields = Collections.emptyList();

        private Set<Component> superComponents = Collections.emptySet();

        public static Builder builder() {
            return new Builder();
        }

        public Builder clazz(Class<?> clazz) {
            this.clazz = clazz;
            return this;
        }

        public Builder annotations(List<Annotation> anns) {
            if(CollectionUtils.isEmpty(anns)) {
                anns = Collections.emptyList();
            } else {
                anns = Collections.unmodifiableList(anns);
            }
            this.annotations = anns;
            return this;
        }

        public Builder componentName(String componentName) {
            this.componentName = componentName;
            return this;
        }

        public Builder propertyFields(List<PropertyField> propertyFields) {
            if(CollectionUtils.isEmpty(propertyFields)) {
                propertyFields = Collections.emptyList();
            } else {
                propertyFields = Collections.unmodifiableList(propertyFields);
            }
            this.propertyFields = propertyFields;
            return this;
        }

        public Builder superComponents(Set<Component> superComponents) {
            if(CollectionUtils.isEmpty(superComponents)) {
                superComponents = Collections.emptySet();
            } else {
                superComponents = Collections.unmodifiableSet(superComponents);
            }
            this.superComponents = superComponents;
            return this;
        }


        public Component build() {
            List<Component> sc = new ArrayList<>(superComponents.size());
            sc.addAll(superComponents);
            return new Component(clazz, annotations, componentName, propertyFields, sc);
        }

    }




}
