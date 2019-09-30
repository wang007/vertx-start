package example;

import io.vertx.core.Future;
import me.wang007.annotation.Deploy;
import me.wang007.verticle.HttpServerVerticle;

/**
 * me.wang007.example {@code &} test
 *
 * created by wang007 on 2019/2/27
 */
@Deploy(instances = Integer.MAX_VALUE)
public class TestHttpServer extends HttpServerVerticle {

    private DemoClient client;


    @Override
    protected void init(Future<Void> initFuture) {
        //initial DemoClient
        initFuture.complete();//通知initial完毕
    }

    public DemoClient getClient() {
        return client;
    }

    public static class DemoClient {

    }


}
