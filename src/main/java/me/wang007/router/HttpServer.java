package me.wang007.router;

import me.wang007.annotation.Deploy;
import me.wang007.verticle.HttpServerVerticle;

/**
 * created by wang007 on 2019/7/26
 */
@Deploy
public class HttpServer extends HttpServerVerticle {

    @Override
    public <T extends HttpServerVerticle> T self() {
        return super.self();
    }
}
