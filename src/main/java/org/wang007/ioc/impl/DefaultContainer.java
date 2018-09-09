package org.wang007.ioc.impl;

import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wang007.boot.VertxBoot;
import org.wang007.exception.VertxStartException;
import org.wang007.ioc.Container;
import org.wang007.ioc.component.ComponentAndFieldsDescription;
import org.wang007.utils.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentMap;

/**
 * 容器的默认实现
 * <p>
 * 启动容器的时候， 需要先设置{@link #setBasePaths(List)} 和 {@link #appendProperties(Map)} 添加属性
 * 这一步在{@link VertxBoot}中完成
 * <p>
 * <p>
 * created by wang007 on 2018/9/9
 */
public class DefaultContainer extends AbstractContainer implements Container {

    private static final Logger logger = LoggerFactory.getLogger(DefaultContainer.class);

    private final Vertx vertx;

    public DefaultContainer(Vertx vertx) {
        this.vertx = vertx;
    }


    @Override
    public <T> T getComponent(String name) throws ClassCastException {
        if (StringUtils.isEmpty(name)) {
            throw new VertxStartException("name required...");
        }
        assertInit();
        ConcurrentMap<String, ComponentAndFieldsDescription> plainKvs0 = super.plainKvs();
        ComponentAndFieldsDescription cd = plainKvs0.get(name);
        if(cd == null) return null;
        return (T) instanceMap().get(cd);
    }

    @Override
    public <T> T getComponent(String name, Class<T> requireType) throws ClassCastException {
        if (StringUtils.isEmpty(name)) {
            throw new VertxStartException("name required...");
        }
        Objects.requireNonNull(requireType, "require requireType");
        Object component = getComponent(name);
        return (T) component;
    }

    @Override
    public <T> List<T> getComponent(Class<T> requireType) throws ClassCastException {
        Objects.requireNonNull(requireType, "require requireType.");
        assertInit();
        List<ComponentAndFieldsDescription> findCds = new ArrayList<>();
        plains().forEach(cd -> {
            if(cd.clazz.equals(requireType) || cd.superClasses.contains(requireType)) findCds.add(cd);
        });
        if(findCds.size() == 0) return Collections.emptyList();
        if(findCds.size() == 1) {
            ComponentAndFieldsDescription cd = findCds.get(0);
            return Collections.singletonList((T)instanceMap().get(cd));
        } else {
            List<T> instances = new ArrayList<>(findCds.size());
            findCds.forEach(cd -> {
                Object instance = instanceMap().get(cd);
                if(instance != null) instances.add((T)instance);
            });
            return instances;
        }
    }

    @Override
    public String getProperty(String key) {
        Objects.requireNonNull(key, "require key...");
        return getProperties().get(key);
    }

    @Override
    public Vertx vertx() {
        assertInit();
        return vertx;
    }

    @Override
    public Map<String, String> getProperties() {
        assertInit();
        return super.getProperties();
    }
}
