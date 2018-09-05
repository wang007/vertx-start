package org.wang007.ioc;

import org.wang007.exception.CreateComponentDescriptionExceptioin;
import org.wang007.ioc.component.ComponentAndFieldsDescription;
import org.wang007.ioc.component.ComponentDescription;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * created by wang007 on 2018/8/26
 *
 * 由于扫描class组件的 类注解和属性 分开执行的   所以会有两个方法
 *
 */
public interface ComponentDescriptionFactory {


    /**
     * 创建一个组件描述 不包括field描述
     *
     * @param clz 组件class
     *
     * @return 组件描述实例
     *
     * @throws CreateComponentDescriptionExceptioin
     *
     */
    <T extends ComponentDescription> T createComponentDescr(Class<?> clz);

    /**
     *
     * 创建一个包括field描述的组件描述
     *
     * @param cd
     *
     * @return
     */
    <T extends ComponentDescription, E extends ComponentAndFieldsDescription> E createComponentAndFieldsDescr(T cd);
}
