package me.wang007.router;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

/**
 * LoadRouter skeleton
 *
 * created by wang007 on 2019/4/12
 */
public abstract class AbstractLoadRouter implements LoadRouter {


    protected Router router;

    protected Vertx vertx;


    @Override
    public void init(Router router, Vertx vertx) {
        this.router = router;
        this.vertx = vertx;
    }

    @Override
    public void start(Future<Void> future) {
        start();
        future.complete();
    }


    public void start() {}



}
