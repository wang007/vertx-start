package org.wang007.ioc.impl;

import io.vertx.core.Verticle;
import org.wang007.ioc.component.ComponentAndFieldsDescription;
import org.wang007.router.LoadRouter;

import java.util.List;

/**
 * created by wang007 on 2018/8/29
 */
public class ComponentDescrCollections {

    /**
     * LoadRouter的组件描述集合
     */
    private List<? extends ComponentAndFieldsDescription> loadRouterCds;

    /**
     * Verticle的组件描述集合
     */
    private List<? extends ComponentAndFieldsDescription> verticleCds;

    /**
     * 除LoadRouter，Verticle的组件描述集合
     */
    private List<? extends ComponentAndFieldsDescription> plainCds;

    public List<? extends ComponentAndFieldsDescription> getLoadRouterCds() {
        return loadRouterCds;
    }

    public void setLoadRouterCds(List<? extends ComponentAndFieldsDescription> loadRouterCds) {
        this.loadRouterCds = loadRouterCds;
    }

    public List<? extends ComponentAndFieldsDescription> getVerticleCds() {
        return verticleCds;
    }

    public void setVerticleCds(List<? extends ComponentAndFieldsDescription> verticleCds) {
        this.verticleCds = verticleCds;
    }

    public List<? extends ComponentAndFieldsDescription> getPlainCds() {
        return plainCds;
    }

    public void setPlainCds(List<? extends ComponentAndFieldsDescription> plainCds) {
        this.plainCds = plainCds;
    }
}
