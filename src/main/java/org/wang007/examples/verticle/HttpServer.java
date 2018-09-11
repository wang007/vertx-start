package org.wang007.examples.verticle;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import org.wang007.annotation.Deploy;
import org.wang007.verticle.HttpServerVerticle;

/**
 * created by wang007 on 2018/9/10
 */
@Deploy(instances = 8)
public class HttpServer extends HttpServerVerticle {

    @Override
    public Handler<AsyncResult<String>> deployedHandler() {
        return ar -> {
            if(ar.succeeded()) {
                System.out.println("fuck you. deploy success.");
            }
        };
    }
}
