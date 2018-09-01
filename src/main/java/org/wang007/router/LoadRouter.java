package org.wang007.router;

import io.vertx.core.Vertx;


import io.vertx.ext.web.Router;
import org.wang007.annotation.Route;

/**
 * 用于装载{@link io.vertx.ext.web.Route}， 使用{@link LoadRouter} 标识是个router，在启动的进行扫描
 *
 * new -> initial -> 对所有的order排序 -> start
 *
 *
 * Created by wang007 on 2018/8/21.
 */
public interface LoadRouter {

    /**
     *
     * @param router 当使用{@link Route#mountPath()} 挂载路径， router为subRouter, (子路由)
     *
     */
    void start(Router router, Vertx vertx);

    /**
     *
     * @return 用于 {@link LoadRouter} 排序， 升序。 默认: 0.
     */
    default int order() {
        return 0 ;
    }

    /**
     * {@link LoadRouter}创建好后调用。
     *
     * @param mainRouter 主路由器， 区别于start方法中的router。 如果未挂载， 与start方法中的router相等。
     */
    default void initial(Router mainRouter, Vertx vertx) {}

}
