package org.wang007.ioc.impl;

import org.wang007.exception.NewInstanceException;
import org.wang007.ioc.ComponentFactory;
import org.wang007.ioc.component.ComponentAndFieldsDescription;

/**
 *
 *
 * created by wang007 on 2018/8/30
 */
public class ComponentFactoryImpl extends AbstractComponentDescriptionFactory implements ComponentFactory {

    @Override
    public <T extends ComponentAndFieldsDescription> Object newInstance(T cd) {
        try {
           return cd.clazz.newInstance();
        } catch (Exception e) {
            throw new NewInstanceException("new instance failed, " + cd.clazz);
        }
    }
}
