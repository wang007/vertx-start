package org.wang007.ioc.impl;

import org.wang007.ioc.InternalContainer;
import org.wang007.ioc.component.ComponentAndFieldsDescription;

import java.util.ArrayList;
import java.util.List;

/**
 * created by wang007 on 2018/8/29
 */
public abstract class AbstractContainer implements InternalContainer {

    private List<? extends ComponentAndFieldsDescription> loadRouterCds;

    private List<? extends ComponentAndFieldsDescription> verticleCds;

    private List<? extends ComponentAndFieldsDescription> plainCds;

    @Override
    public void separateComponentDescr(List<? extends ComponentAndFieldsDescription> cds) {
        List<ComponentAndFieldsDescription> loadRouterCds1 = new ArrayList<>();
        List<ComponentAndFieldsDescription> verticleCds1 = new ArrayList<>();
        List<ComponentAndFieldsDescription> plainCds1 = new ArrayList<>();
        cds.forEach(cd -> {
            if(cd.isLoadRouter) loadRouterCds1.add(cd);
            else if(cd.isVertilce) verticleCds1.add(cd);
            else plainCds1.add(cd);
        });
        this.loadRouterCds = loadRouterCds1;
        this.verticleCds = verticleCds1;
        this.plainCds = plainCds1;
    }
}
