package org.wang007.ioc.impl;

import io.vertx.core.Verticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wang007.annotation.*;
import org.wang007.annotation.root.RootForInject;
import org.wang007.exception.ErrorUsedAnnotationException;
import org.wang007.exception.RepetUsedAnnotationException;
import org.wang007.ioc.ComponentDescriptionFactory;
import org.wang007.ioc.component.ComponentAndFieldsDescription;
import org.wang007.ioc.component.ComponentDescription;
import org.wang007.ioc.component.InjectPropertyDescription;
import org.wang007.router.LoadRouter;
import org.wang007.utils.CollectionUtils;
import org.wang007.utils.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;

/**
 * created by wang007 on 2018/8/26
 */
public abstract class AbstractComponentDescriptionFactory implements ComponentDescriptionFactory {

    private static final Logger logger = LoggerFactory.getLogger(AbstractComponentDescriptionFactory.class);


    @Override
    public <T extends ComponentDescription> T createComponentDescr(Class<?> clz, Annotation ann, boolean isSingle,
                                                                   List<Annotation> otherAnns) {
        boolean isVerticleClass = false;
        boolean isLoadRouterClass = false;
        String componentName = null;  //组件名

        //检查 Verticle类的注解是否@Deploy, 检查LoadRouter类的注解是否@Route
        if (Verticle.class.isAssignableFrom(clz)) {  //verticle
            isVerticleClass = true;
            if (!(ann instanceof Deploy)) {
                logger.error("{} 使用了错误的注解", clz.getCanonicalName());
                throw new ErrorUsedAnnotationException("Verticle中只能使用@Deploy注解，不能使用其他组件注解...");
            }
            componentName = StringUtils.replaceFirstUpperCase(clz.getSimpleName());

        } else if (LoadRouter.class.isAssignableFrom(clz)) { //loadRouter
            isLoadRouterClass = true;
            if (!(ann instanceof Route)) {
                logger.error("{} 使用了错误的注解", clz);
                throw new ErrorUsedAnnotationException("LoadRouter中只能使用@Route注解，不能使用其他组件注解...");
            }
            componentName = StringUtils.replaceFirstUpperCase(clz.getSimpleName());
        }

        if (ann instanceof Deploy && !isVerticleClass) { //非Verticle中使用@Deploy
            logger.error("{} 使用了错误的注解", clz);
            throw new ErrorUsedAnnotationException("@Deploy只能注解到Verticle上，普通组件请使用@Component或其他可成为组件的注解");
        } else if (ann instanceof Route && !isLoadRouterClass) {//非LoadRouter中使用@Route
            logger.error("{} 使用了错误的注解", clz);
            throw new ErrorUsedAnnotationException("@Route只能注解到LoadRouter上，普通组件请使用@Component或其他可成为组件的注解");

        }
        if (componentName == null)
            componentName = createComponentName(clz, ann, Collections.unmodifiableList(otherAnns));

        List<Class<?>> superes = findSuperes(clz);


        ComponentDescription.Builder builder = ComponentDescription.Builder.builder();
        ComponentDescription cd = builder.clazz(clz)
                .superClasses(superes)
                .isVertilce(isVerticleClass)
                .isLoadRouter(isLoadRouterClass)
                .annotation(ann)
                .componentName(componentName)
                .isSingle(isSingle)
                .otherAnnotations(otherAnns).build();

        return doCreateComponentDescr(cd);
    }

    /**
     * 递归查找所有的父类
     * @param clz
     * @return
     */
    private List<Class<?>> findSuperes(Class<?> clz) {
        Set<Class<?>> superClasses = new HashSet<>();

        Class<?> superclass = clz.getSuperclass();

        if(superclass == Object.class) {
            Class<?>[] interfaces = clz.getInterfaces();
            superClasses.addAll(Arrays.asList(interfaces));
            return new ArrayList<>(superClasses);
        }

        while (superclass != null) {
            if(superclass == Object.class)
                return new ArrayList<>(superClasses);
            superClasses.add(superclass);
            Class<?>[] interfaces = superclass.getInterfaces();
            superClasses.addAll(Arrays.asList(interfaces));
            superclass = superclass.getSuperclass();
        }
        return new ArrayList<>(superClasses);
    }

    /**
     * 创建组件描述
     *
     * @param cd
     * @param <T>
     * @return
     */
    protected <T extends ComponentDescription> T doCreateComponentDescr(ComponentDescription cd) {
        return (T) cd;
    }


    /**
     * 生成组件名称
     *
     * @param clz
     * @param ann
     * @return
     */
    private String createComponentName(Class<?> clz, Annotation ann, List<Annotation> otherAnnotations) {
        if (ann instanceof Component) {
            Component component = (Component) ann;
            String compoentName = StringUtils.trimToEmpty(component.value());
            if (StringUtils.isEmpty(compoentName))
                compoentName = StringUtils.replaceFirstUpperCase(clz.getSimpleName());
            return compoentName;
        }

        if (ann instanceof ConfigurationProperties) {
            ConfigurationProperties cp = (ConfigurationProperties) ann;
            String compoentName = StringUtils.trimToEmpty(cp.value());
            if (StringUtils.isEmpty(compoentName))
                compoentName = StringUtils.replaceFirstUpperCase(clz.getSimpleName());
            return compoentName;
        }
        return docreateComponentName(clz, ann, otherAnnotations);
    }

    /**
     * 创建组件名 留作拓展
     *
     * @param clz
     * @param ann
     * @param otherAnnotations
     * @return
     */
    protected String docreateComponentName(Class<?> clz, Annotation ann, List<Annotation> otherAnnotations) {
        //以后拓展其他注解，可以继续往里面加
        return StringUtils.replaceFirstUpperCase(clz.getSimpleName());
    }


    @Override
    public <T extends ComponentDescription, E extends ComponentAndFieldsDescription> E createComponentAndFieldsDescr(T cd) {

        Class<?> clz = cd.clazz;
        //获取所有的fields, 分离中需要inject的属性
        List<Field> fields = getAllFields(clz);

        if (CollectionUtils.isEmpty(fields)) {
           return (E) ComponentAndFieldsDescription.Builder.builder(cd).propertyDescriptions(null).build();

        }
        List<InjectPropertyDescription> ipds = new ArrayList<>();

        if (cd.annotation instanceof Route || cd.annotation instanceof Deploy
                || cd.annotation instanceof Component) {

            fields.forEach(f -> {
                List<Annotation> otherAnns = new ArrayList<>();
                Annotation injectTypeAnn = handleFieldAnns(cd, f, otherAnns);
                if (injectTypeAnn == null) return; //如果没有注入型注解， 忽略掉当前field

                InjectPropertyDescription ipd;
                Class<? extends Type> fieldClz = f.getGenericType().getClass();

                if (injectTypeAnn instanceof Value) {
                    String injectKeyName = ((Value) injectTypeAnn).value().trim();

                    ipd = InjectPropertyDescription.Builder.builder()
                            .fieldName(f.getName())
                            .field(f)
                            .fieldClass(fieldClz)
                            .byTypeInject(false)
                            .injectKeyName(injectKeyName)
                            .componentInject(false)
                            .annotation(injectTypeAnn)
                            .otherAnnotations(otherAnns)
                            .build();

                } else if (injectTypeAnn instanceof Inject) {
                    String injectKeyName = ((Inject) injectTypeAnn).value().trim();
                    boolean byTypeInject = StringUtils.isEmpty(injectKeyName); //injectKeyName isEmpty 那么就是byType注入

                    ipd = InjectPropertyDescription.Builder.builder()
                            .fieldName(f.getName())
                            .field(f)
                            .fieldClass(fieldClz)
                            .byTypeInject(byTypeInject)
                            .injectKeyName(injectKeyName)
                            .componentInject(true)
                            .annotation(injectTypeAnn)
                            .otherAnnotations(otherAnns)
                            .build();
                } else {
                    ipd = elseCreateInjectPropDescr(cd, f);
                }
                if (ipd != null) ipds.add(ipd);
            });

        } else if (cd.annotation instanceof ConfigurationProperties) { //属性集合的注解，不管属性有没有用注入的注解，全部都要解析

            ConfigurationProperties cfpAnn = (ConfigurationProperties) cd.annotation;
            String prefix = cfpAnn.prefix().trim();
            fields.forEach(f -> {
                String fieldName = f.getName();
                Class<? extends Type> fieldClz = f.getGenericType().getClass();

                List<Annotation> otherAnns = new ArrayList<>();

                Annotation injectTypeAnn = handleFieldAnns(cd, f, otherAnns);

                String injectKeyName;
                InjectPropertyDescription ipd;

                if (injectTypeAnn == null || injectTypeAnn instanceof Value) {  //值注入 值注入只有byName
                    injectKeyName = injectTypeAnn == null ? fieldName : ((Value) injectTypeAnn).value();
                    injectKeyName = StringUtils.isEmpty(injectKeyName) ? prefix : prefix + '.' + injectKeyName;

                    ipd = InjectPropertyDescription.Builder.builder()
                            .fieldName(fieldName)
                            .field(f)
                            .fieldClass(fieldClz)
                            .byTypeInject(false)
                            .injectKeyName(injectKeyName)
                            .componentInject(false)
                            .annotation(injectTypeAnn)
                            .otherAnnotations(otherAnns)
                            .build();

                } else if (injectTypeAnn instanceof Inject) {   //组件注入
                    injectKeyName = ((Inject) injectTypeAnn).value().trim();
                    boolean byTypeInject = StringUtils.isEmpty(injectKeyName); //injectKeyName isEmpty 那么就是byType注入

                    ipd = InjectPropertyDescription.Builder.builder()
                            .fieldName(fieldName)
                            .field(f)
                            .fieldClass(fieldClz)
                            .byTypeInject(byTypeInject)
                            .injectKeyName(injectKeyName)
                            .componentInject(true)
                            .annotation(injectTypeAnn)
                            .otherAnnotations(otherAnns)
                            .build();
                } else { //
                    ipd = elseCreateInjectPropDescr(cd, f);
                }
                if (ipd != null) ipds.add(ipd);
            });

        } else {    //其他注入型注解
            fields.forEach(f -> {
                InjectPropertyDescription ipd = elseCreateInjectPropDescr(cd, f);
                if (ipd != null) ipds.add(ipd);
            });
        }
        return (E) ComponentAndFieldsDescription.Builder.builder(cd).propertyDescriptions(ipds).build();
    }

    /**
     * 处理属性上的注解  非注入型注解，直接加到otherAnns
     *
     * @param cd
     * @param field
     * @param otherAnns
     * @return 注入型注解
     */
    private Annotation handleFieldAnns(ComponentDescription cd, Field field, List<Annotation> otherAnns) {
        Annotation[] fAnn = field.getDeclaredAnnotations();
        boolean isExistInjectType = false;
        Annotation injectTypeAnn = null;

        for (Annotation ann : fAnn) {
            Class<? extends Annotation> annClz = ann.annotationType();
            //注入型注解 只允许存在一个
            RootForInject rootAnno = annClz.getAnnotation(RootForInject.class);
            if (rootAnno != null) {
                if (isExistInjectType)
                    throw new RepetUsedAnnotationException(cd.clazz + ": " + field.getName() + " 重复使用注入型注解");
                isExistInjectType = true;
                injectTypeAnn = ann;
                continue;
            }
            //非注入型注解， 也需要保存起来
            otherAnns.add(ann);
        }
        return injectTypeAnn;
    }


    /**
     * 其他情况创建 注入属性描述类
     * {@link #createComponentAndFieldsDescr(ComponentDescription)} 中不满足的预定好的注解  额外拓展的注解可以使用该方法
     *
     * @param cd    组件描述
     * @param field 对应class的某一属性
     * @param <T>
     * @param <E>
     * @return 注入属性描述  == null, 直接丢弃，  != null 将会保存到组件描述的类中
     */
    protected <T extends ComponentDescription, E extends InjectPropertyDescription> E
    elseCreateInjectPropDescr(T cd, Field field) {
        return null;
    }


    /**
     * 查询所有的field属性。包括所有父类的属性
     * <p>
     * 包括处理{@link InjectToSuper} 注解，加载父类中的
     *
     * @param clz
     * @return
     */
    private List<Field> getAllFields(Class<?> clz) {

        Field[] fields = clz.getDeclaredFields(); //所有class的所有属性。包括private， static

        List<Field> fieldList = new ArrayList<>();
        for (Field f : fields) {
            if (Modifier.isStatic(f.getModifiers())) continue; //过滤掉static属性
            fieldList.add(f);
        }
        Class<?> superclass = clz.getSuperclass();
        if (superclass.equals(Object.class)) return fieldList;

        //如果父类中不存在@InjectToSuper, 不处理父类及父类以上的类
        InjectToSuper injectToSuper = superclass.getAnnotation(InjectToSuper.class);
        if (injectToSuper == null) return fieldList;

        fieldList.addAll(getAllFields(superclass));
        return fieldList;
    }


}
