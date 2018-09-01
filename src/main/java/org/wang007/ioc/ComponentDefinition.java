package org.wang007.ioc;

import io.vertx.core.Vertx;

/**
 * 组件定义，通过该接口的{@link #supplyComponent(Vertx)} 生成一个实例，加到容器中
 *
 * 通过这个接口，将其他的第三方组件加到容器中
 *
 * created by wang007 on 2018/8/22
 */
public interface ComponentDefinition<T> {

    T supplyComponent(Vertx vertx);
}
