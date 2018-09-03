package org.wang007.router;

import io.vertx.core.Vertx;


import io.vertx.ext.web.Router;
import org.wang007.annotation.Route;

/**
 * 用于装载{@link io.vertx.ext.web.Route}， 使用{@link LoadRouter} 标识是个router，在启动的进行扫描
 *
 * 多实例的verticle中，每个verticle中的LoadRouter、{@link Router}都是独立的。 强制放到一起，会发生并发问题。
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
     * 例如：权限相关的route实现，可以放到该方法中。
     *
     *
     * @param router 路由器， 跟{@link #start(Router, Vertx)}中的是同一个router.
     */
    default void initial(Router router, Vertx vertx) {}

}
