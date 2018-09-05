package org.wang007.ioc.impl;


import io.vertx.core.Vertx;
import io.vertx.core.VertxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wang007.annotation.Name;
import org.wang007.exception.InjectException;

import org.wang007.init.Initializable;
import org.wang007.ioc.ComponentDefinition;
import org.wang007.ioc.InternalContainer;
import org.wang007.ioc.component.ComponentAndFieldsDescription;
import org.wang007.ioc.component.ComponentDescription;
import org.wang007.ioc.component.InjectPropertyDescription;
import org.wang007.parse.ComponentParse;
import org.wang007.utils.CheckUtil;
import org.wang007.utils.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * created by wang007 on 2018/8/29
 */
public abstract class AbstractContainer implements InternalContainer {


    private static final String Component_Define_Method_Name = "supplyComponent";


    private static final Logger logger = LoggerFactory.getLogger(AbstractContainer.class);


    /**
     * loadRouter组件集合  loadRouter不需要作为组件注入到其他组件中
     */
    private List<? extends ComponentAndFieldsDescription> loadRouters;

    /**
     * verticle组件集合   verticle不需要作为组件注入到其他组件中
     */
    private List<? extends ComponentAndFieldsDescription> verticles;

    //=========================================================

    /**
     * 普通组件集合
     */
    private List<ComponentAndFieldsDescription> plains = new ArrayList<>();

    /**
     * 普通的组件 map
     * key: componentName
     * value: 组件描述
     */
    private ConcurrentMap<String, ComponentAndFieldsDescription> plainKvs = new ConcurrentHashMap<>();

    /**
     * 实例集合
     * <p>
     * key: 组件描述。
     * value: 单利模式的实例
     */
    private ConcurrentMap<ComponentAndFieldsDescription, Object> instanceMap = new ConcurrentHashMap<>();

    private ComponentParse parse = ComponentParse.create();

    @Override
    public void initComponents(List<? extends ComponentAndFieldsDescription> cds) {
        List<ComponentAndFieldsDescription> loadRouters1 = new ArrayList<>();
        List<ComponentAndFieldsDescription> verticles1 = new ArrayList<>();

        Map<ComponentAndFieldsDescription, Object> componentDefines = new HashMap<>();  //组件定义的组件描述
        List<MidStateComponent> mids = new ArrayList<>(cds.size());     //临时保存

        cds.forEach(cd -> {
            if (cd.isLoadRouter) {
                loadRouters1.add(cd);

            } else if (cd.isVertilce) {
                verticles1.add(cd);

            } else {
                Object instance = parse.newInstance(cd);    //实例化
                checkAndSaveComponent(cd, instance);

                MidStateComponent mid = new MidStateComponent(instance, cd);
                mids.add(mid);
                if (cd.propertyDescriptions.size() == 0) {
                    mid.initialCompleted(); //初始化完成
                }
                if (instance instanceof ComponentDefinition) { //组件描述  组件提供者，且初始化完成.
                    if (mid.isInitialed()) {
                        ComponentDefineTuple tuple = initComponentDefine(cd, instance);
                        checkAndSaveComponent(tuple.cd, tuple.instance);
                    } else {
                        componentDefines.put(cd, instance);     //组件描述 临时保存
                    }
                }
            }
        });
        loadRouters = Collections.unmodifiableList(loadRouters1);
        verticles = Collections.unmodifiableList(verticles1);

    }


    /**
     * @param cd
     * @param instance
     */
    private void checkAndSaveComponent(ComponentAndFieldsDescription cd, Object instance) {

        ComponentAndFieldsDescription old = plainKvs.put(cd.componentName, cd);     //把组件名作为key 保存起来
        if (old != null)
            throw new VertxException("componentName: " + cd.componentName + " already exist, old class: "
                    + cd.componentName + ", new class: " + old);

        if (instance == null) throw new VertxException("new Instance failed, class -> " + cd.clazz);

        instanceMap.put(cd, instance);    //把单例保存起来。

    }

    /**
     * 执行初始化组件
     *
     * @param initializable
     */
    private void executeInitial(Object initializable) {
        if (initializable instanceof Initializable) {
            Initializable init = (Initializable) initializable;
            init.initial(vertx());
        } else {
            if (logger.isTraceEnabled()) {
                logger.trace("class: {} is not Initializable.");
            }

        }

    }


    /**
     * 调用{@link ComponentDefinition#supplyComponent(Vertx)} 生成新的组件。
     *
     * @param defineCd 组件提供者的组件描述
     * @param instance 组件描述的实例
     * @return 组件描述，组件实例
     */
    private ComponentDefineTuple initComponentDefine(ComponentAndFieldsDescription defineCd, Object instance) {
        String componentNameForAnn = null;
        try {
            Method method = defineCd.clazz.getDeclaredMethod(Component_Define_Method_Name, Vertx.class);
            Name annotation = method.getAnnotation(Name.class);
            componentNameForAnn = annotation.value();
        } catch (NoSuchMethodException e) {
            //NOOP
        }

        ComponentDefinition definition = (ComponentDefinition) instance;
        executeInitial(definition); //
        Object component = definition.supplyComponent(vertx());

        Class<?> componentClass = component.getClass();
        CheckUtil.CheckTypeForComponent(componentClass); //检查校验
        ComponentAndFieldsDescription cd = parse.createComponent(componentClass);

        if (StringUtils.isBlank(componentNameForAnn)) {
            return new ComponentDefineTuple(cd, component);
        }

        ComponentAndFieldsDescription.Builder builder = ComponentAndFieldsDescription.Builder.builder(cd);
        builder.componentName(componentNameForAnn);
        builder.propertyDescriptions(cd.propertyDescriptions);
        return new ComponentDefineTuple(builder.build(), instance);
    }


    @Override
    public Object inject(ComponentAndFieldsDescription cd, Object instance) {
        cd.propertyDescriptions.forEach(ipd -> {
            if (ipd.componentInject) {   //组件注入。
                ComponentAndFieldsDescription findCd = matchingComponent(ipd.fieldClass);
                if (findCd == null) {
                    logger.warn("inject failed. not found component for inject.");
                    return;
                }
                if (findCd.isSingle) {   //单例模式
                    Object componentInstance = instanceMap.get(findCd);
                    if (!findCd.clazz.isInstance(componentInstance)) {
                        throw new InjectException("type match failed...");
                    }
                    safeSet(ipd.field, instance, componentInstance);

                } else {    //多例
                    Object multiInstance = parse.newInstance(findCd);
                    safeSet(ipd.field, instance, multiInstance);
                    //临时保存多例instance。作为本次注入的instance
                    Map<ComponentAndFieldsDescription, Object> multiInstanceKv = new HashMap<>();
                    multiInstanceKv.put(findCd, multiInstance);


                }
            } else {  //属性注入。
                String value = getProperty(ipd.injectKeyName);
                injectValue0(cd, ipd, instance, value);
            }
        });
        return instance;
    }


    private Object injectForComponentMulti(ComponentAndFieldsDescription cd, Object instance,
                                           Map<ComponentAndFieldsDescription, Object> multiInstanceKv) {
        return null;
    }


    private void injectValue0(ComponentAndFieldsDescription cd,
                              InjectPropertyDescription ipd, Object instance, String value) {
        boolean inject = beforeInjectValue(cd, ipd, instance, value);
        if (inject) {
            logger.debug("before inject value done...");
            return;
        }
        if (value == null) {
            logger.warn("class: {}, field: {}  inject value failed, not found value...", cd.clazz, ipd.fieldName);
            return;
        }
        try {
            Class<?> fieldClass = ipd.fieldClass;

            if (fieldClass == String.class) {
                ipd.field.set(instance, value);
            } else if (fieldClass.isPrimitive()) { //基本类型

                if (fieldClass == Integer.TYPE) {
                    ipd.field.setInt(instance, Integer.valueOf(value));

                } else if (fieldClass == Short.TYPE) {
                    ipd.field.setShort(instance, Short.valueOf(value));

                } else if (fieldClass == Float.TYPE) {
                    ipd.field.setFloat(instance, Float.valueOf(value));

                } else if (fieldClass == Boolean.TYPE) {
                    ipd.field.setBoolean(instance, Boolean.valueOf(value));

                } else if (fieldClass == Long.TYPE) {
                    ipd.field.setLong(instance, Long.valueOf(value));

                } else if (fieldClass == Double.TYPE) {
                    ipd.field.setDouble(instance, Double.valueOf(value));

                } else if (fieldClass == Character.TYPE) {
                    ipd.field.setChar(instance, value.length() > 0 ? value.charAt(0) : ' ');

                } else if (fieldClass == Byte.TYPE) {
                    ipd.field.setByte(instance, Byte.valueOf(value));
                }

            } else if (fieldClass == Integer.class) {
                ipd.field.set(instance, Integer.valueOf(value));

            } else if (fieldClass == Short.class) {
                ipd.field.set(instance, Short.valueOf(value));

            } else if (fieldClass == Boolean.class) {
                ipd.field.set(instance, Boolean.valueOf(value));

            } else if (fieldClass == Long.class) {
                ipd.field.set(instance, Long.valueOf(value));

            } else if (fieldClass == Float.class) {
                ipd.field.set(instance, Float.valueOf(value));
            } else {    //其他类型不处理
                logger.warn("not known type.  class: " + cd.clazz + ",  field: " + ipd.fieldName + ", field-type:" + ipd.fieldClass);
            }
        } catch (IllegalAccessException e) {
            throw new InjectException("illegal access, inject value failed.  class: " + cd.clazz + ",  field: " + ipd.fieldName + ", field-type:" + ipd.fieldClass);
        }
    }

    /**
     * @param cd       组件描述
     * @param ipd      需要注入的属性描述
     * @param instance 组件实例
     * @param value    被注入的value
     * @return true: 已完成注入，接下来将不会做注入了。 false: 未完成注入，接下来继续做注入。
     */
    protected boolean beforeInjectValue(ComponentAndFieldsDescription cd,
                                        InjectPropertyDescription ipd, Object instance, String value) {
        return false;
    }

    /**
     * 根据clazz 匹配组件描述
     *
     * @param clazz
     * @return 梦游
     */
    private ComponentAndFieldsDescription matchingComponent(Class<?> clazz) {
        boolean exist = false;
        ComponentAndFieldsDescription find = null;
        for (ComponentAndFieldsDescription plain : plains) {
            if (plain.clazz == clazz || plain.superClasses.contains(clazz)) {
                if (exist)
                    throw new InjectException("inject failed. class: " + clazz + " found non-unique." + " sub-class [ " + find.clazz + ", " + plain.clazz + "]");
                exist = true;
                find = plain;
            }
        }
        return find;
    }

    private void safeSet(Field field, Object instance, Object value) {
        try {
            field.set(instance, value);
        } catch (IllegalAccessException e) {
            throw new InjectException("illegal access, inject value failed. field: " + field.getName());

        }
    }


    static class ComponentDefineTuple {

        public final ComponentAndFieldsDescription cd;

        public final Object instance;

        public ComponentDefineTuple(ComponentAndFieldsDescription cd, Object instance) {
            this.cd = cd;
            this.instance = instance;
        }
    }


}
