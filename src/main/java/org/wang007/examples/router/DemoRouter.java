package org.wang007.examples.router;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import org.wang007.annotation.Route;
import org.wang007.router.LoadRouter;

/**
 * created by wang007 on 2018/9/10
 */
@Route(mountPath = "/demo")
public class DemoRouter implements LoadRouter {

    @Override
    public void start(Router router, Vertx vertx) {

        router.route("/test1").handler(rc -> {

            rc.response().end("hello world");

        });

    }
}
