package me.wang007.router;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import me.wang007.verticle.HttpServerVerticle;

/**
 * LoadRouter skeleton
 *
 * @param <T> 实际项目中{@link HttpServerVerticle}的子类
 *
 * created by wang007 on 2019/4/12
 */
public abstract class AbstractLoadRouter<T extends HttpServerVerticle> implements LoadRouter {

    protected Router router;

    protected Vertx vertx;


    @Override
    public final void init(Router router, Vertx vertx, HttpServerVerticle server) {
        this.router = router;
        this.vertx = vertx;
        init((T) server);
    }


    protected void init(T server) {}


    @Override
    public void start(Future<Void> future) {
        start();
        future.complete();
    }


    public void start() {}
}
