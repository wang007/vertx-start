package org.wang007.ioc;

import org.wang007.ioc.component.ComponentAndFieldsDescription;
import org.wang007.ioc.impl.ComponentDescrCollections;

import java.util.List;

/**
 * created by wang007 on 2018/8/29
 */
public interface InternalContainer extends Container {

    /**
     * 分离出verticle，LoadRouter两类component组件
     *
     * @param cds
     */
     void separateComponentDescr(List<? extends ComponentAndFieldsDescription> cds);

}
