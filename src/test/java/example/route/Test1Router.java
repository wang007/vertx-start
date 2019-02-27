package example.route;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import me.wang007.annotation.Route;
import me.wang007.router.LoadRouter;

/**
 * created by wang007 on 2019/2/27
 */
@Route("/t1")
public class Test1Router implements LoadRouter {

    @Override
    public void start(Router router, Vertx vertx) {

        router.route("/hello").handler(rc -> {
            rc.response().end("hello world");
        });

        router.route("/world").handler(rc -> {
            rc.response().end("hello world...");
        });
    }
}
