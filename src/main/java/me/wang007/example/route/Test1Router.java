package me.wang007.example.route;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import me.wang007.annotation.Route;
import me.wang007.example.TestHttpServer;
import me.wang007.router.AbstractLoadRouter;
import me.wang007.router.LoadRouter;
import me.wang007.verticle.HttpServerVerticle;

/**
 * created by wang007 on 2019/2/27
 */
@Route("/t1")
public class Test1Router extends AbstractLoadRouter {


    private TestHttpServer.DemoClient client;

    @Override
    public void start() {

        router.route("/hello").handler(rc -> {
            System.out.println("thread -> " + Thread.currentThread().getName());
            rc.response().end("hello world");
        });

        router.route("/world").handler(rc -> {
            System.out.println("thread -> " + Thread.currentThread().getName());
            rc.response().end("hello world...");
        });
    }

    @Override
    protected void init(HttpServerVerticle server) {
        super.init(server);
        TestHttpServer self = server.self();
        client = self.getClient();
    }
}
