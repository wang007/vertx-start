package org.wang007.ioc;

import org.wang007.ioc.component.ComponentAndFieldsDescription;
import org.wang007.ioc.impl.ComponentFactoryImpl;

/**
 * Component工厂
 *
 * created by wang007 on 2018/8/24
 */
public interface ComponentFactory extends ComponentDescriptionFactory {

    /**
     * 根据组件描述创建实例
     *
     * @param cd
     * @param <T>
     * @return
     */
    <T extends ComponentAndFieldsDescription> Object newInstance(T cd) ;


    /**
     * 创建一个组件工厂
     *
     * @return
     */
    static ComponentFactory create() {
        return new ComponentFactoryImpl();
    }


}
