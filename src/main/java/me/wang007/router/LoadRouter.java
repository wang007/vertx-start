package me.wang007.router;

import io.vertx.core.Future;
import io.vertx.core.Vertx;


import io.vertx.ext.web.Router;
import me.wang007.annotation.Route;

/**
 * 用于装载{@link io.vertx.ext.web.Route}， 使用{@link LoadRouter} 标识是个router，在启动的进行扫描
 *
 * 多实例的verticle中，每个verticle中的LoadRouter、{@link Router}都是独立的。 强制放到一起，会发生并发问题。
 *
 * new -> init -> 对所有的order排序 -> start
 *
 *
 * Created by wang007 on 2018/8/21.
 */
public interface LoadRouter {

    /**
     * {@link LoadRouter}生命周期方法。
     *
     * 当且仅当{@link #start(Future)}成功完成， 调用入参中的{@link Future#complete()}
     *
     */
    void start(Future<Void> future);

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
     * @param router 当使用{@link Route#mountPath()} 挂载路径， router为subRouter, (子路由)
     */
    default void init(Router router, Vertx vertx) {}

}
