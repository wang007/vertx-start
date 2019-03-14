package me.wang007.boot;

import io.vertx.core.Vertx;
import me.wang007.container.Container;
import me.wang007.exception.VertxStartException;

import java.util.Map;

/**
 * 简易的vertxBoot
 *
 * created by wang007 on 2019/3/14
 */
public class SimpleVertxBoot implements VertxBoot {

    private final Container container;

    private final PropertiesLoader prLoader;

    private final Map<String, String> properties;

    private final Vertx vertx;

    SimpleVertxBoot(Container container, PropertiesLoader prLoader, Map<String, String> properties, Vertx vertx) {
        this.container = container;
        this.prLoader = prLoader;
        this.properties = properties;
        this.vertx = vertx;
    }

    @Override
    public Container getContainer() {
        return container;
    }

    @Override
    public String getProperty(String key) {
        return properties.get(key);
    }

    @Override
    public Map<String, String> getProperties() {
        return properties;
    }

    @Override
    public <E> E loadFor(Class<E> propertiesClz) {
        return prLoader.loadFor(propertiesClz, properties);
    }

    @Override
    public Vertx vertx() {
        return vertx;
    }

    @Override
    public VertxBoot start() {
        throw new VertxStartException("Vertx boot already start.");
    }
}
