package me.wang007.container;


import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * created by wang007 on 2019/2/26
 */
public class DefaultContainer extends AbstractLoadContainer<DefaultContainer> implements Container {


    private AtomicBoolean started = new AtomicBoolean(false);

    @Override
    public Component getComponent(Class<?> targetClz) {
        Objects.requireNonNull(targetClz, "require not null");
        return componentMap().get(targetClz);
    }

    @Override
    public List<Component> getComponentsByAnnotation(Class<? extends Annotation> loadBy) {
        Objects.requireNonNull(loadBy, "require not null");
        List<Component> components = new ArrayList<>();
        componentMap().forEach((clz, component) -> {
            if(component.annotationBy(loadBy)) components.add(component);
        });
        return components;
    }

    @Override
    public List<Component> getComponentsFrom(Class<?> fromClz) {
        Objects.requireNonNull(fromClz, "require not null");
        List<Component> components = new ArrayList<>();
        componentMap().forEach((clz, component) -> {
            if(component.superFrom(fromClz)) components.add(component);
        });
        return components;
    }

    @Override
    public Container start(String... basePaths) {
        if(started.compareAndSet(false, true)) {
            logger.info("container starting...");
            loadComponents(basePaths);
            logger.info("container started completely");
        }
        return this;
    }

    @Override
    public boolean started() {
        return started.get();
    }
}
