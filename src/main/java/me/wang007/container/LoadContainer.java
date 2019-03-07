package me.wang007.container;

import java.lang.annotation.Annotation;

/**
 * 可注册的组件容器
 *
 * 该类主要作用：
 * 1. 对需要加载的目标组件进行注册
 * 2. 启动容器
 *
 * note: 扫描加载组件时，避免触发类的初始化
 *
 * created by wang007 on 2019/2/26
 */
public interface LoadContainer<E extends LoadContainer> extends Container {

    /**
     * 指定加载的类 是否被目标注解 注解上。
     *
     * note: 如果已加载过组件，则不做加载操作。
     *
     * @param loadBy 目标注解
     */
     E registerLoadBy(Class<? extends Annotation> loadBy);


    /**
     * 指定加载的类
     *
     * note: 如果已加载过组件，则不做加载操作。
     *
     * @param targetClz 目标类
     */
    E register(Class<?> targetClz);


    /**
     * 指定加载类的子类 不包括其本身
     *
     * 不加载其本身，如果加载其本身的话，可以{@link #register(Class)} 指定
     *
     * note: 如果已加载过组件，则不做加载操作。
     *
     *
     * @param fromClz 目标类
     * @return
     */
    E registerFrom(Class<?> fromClz);

    /**
     * 启动container，加载组件
     *
     * @param basePaths 组件的基路径
     * @return
     */
    Container start(String... basePaths);

    /**
     * 容器是否完成启动
     *
     * @return true：未启动， false: 已启动
     */
    boolean started();


}
