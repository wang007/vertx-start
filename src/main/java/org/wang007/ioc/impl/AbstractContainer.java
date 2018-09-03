package org.wang007.ioc.impl;

import io.vertx.core.Verticle;
import io.vertx.core.VertxException;
import org.wang007.ioc.InternalContainer;
import org.wang007.ioc.component.ComponentAndFieldsDescription;
import org.wang007.parse.ComponentParse;
import org.wang007.router.LoadRouter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * created by wang007 on 2018/8/29
 */
public abstract class AbstractContainer implements InternalContainer {

    /**
     *  loadRouter组件集合  loadRouter不需要作为组件注入到其他组件中
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
     *
     * key: 组件描述。
     * value: 单利模式的实例
     */
    private ConcurrentMap<ComponentAndFieldsDescription, Object> instances4Single = new ConcurrentHashMap<>();

    private ComponentParse parse = ComponentParse.create();

    @Override
    public void initComponents(List<? extends ComponentAndFieldsDescription> cds) {
        List<ComponentAndFieldsDescription> loadRouters1 = new ArrayList<>();
        List<ComponentAndFieldsDescription> verticles1 = new ArrayList<>();
        cds.forEach(cd -> {
            if(cd.isLoadRouter) loadRouters1.add(cd);
            else if(cd.isVertilce) verticles1.add(cd);
            else {
                plains.add(cd);

                ComponentAndFieldsDescription old = plainKvs.put(cd.componentName, cd);     //把组件名作为key 保存起来
                if(old != null)
                    throw new VertxException("componentName: "+ cd.componentName +" already exist, old class: "
                            + cd.componentName +", new class: " + old);

                if(cd.isSingle) {
                    Object instance = parse.newInstance(cd);
                    if(instance == null) throw new VertxException("new Instance failed, class -> " + cd.clazz);

                    Object oldInstance = instances4Single.put(cd, instance);    //把单例保存起来。

                    if(oldInstance != null) throw new VertxException("instance already exist, class:" + cd.clazz);
                }
            }
        });
        loadRouters = Collections.unmodifiableList(loadRouters1);
        verticles = Collections.unmodifiableList(verticles1);
    }
}
