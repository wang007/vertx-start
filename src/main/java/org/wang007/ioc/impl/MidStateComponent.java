package org.wang007.ioc.impl;

import org.wang007.ioc.component.ComponentAndFieldsDescription;
import org.wang007.ioc.component.InjectPropertyDescription;

import java.util.ArrayList;
import java.util.List;

/**
 * 中间态的实例，新创建的instance.
 *
 * created by wang007 on 2018/9/4
 */
public class MidStateComponent {

    /**
     * 实例
     */
    public final Object instance;

    /**
     * 实例中未初始化的属性
     */
    public final List<InjectPropertyDescription> unInitComponents = new ArrayList<>();


    public final ComponentAndFieldsDescription cdf ;


    /**
     * 初始化完成的标记
     */
    private boolean initialed;

    public MidStateComponent(Object instance, ComponentAndFieldsDescription cdf) {
        this.instance = instance;
        this.cdf = cdf;
    }

    public boolean isInitialed() {
        return initialed;
    }

    public void initialCompleted() {
         this.initialed = true;
    }
}
