package boot;

import io.vertx.core.Vertx;
import me.wang007.boot.VertxComponentLoader;
import me.wang007.container.DefaultContainer;
import me.wang007.verticle.StartVerticleFactory;
import org.junit.Test;

/**
 * created by wang007 on 2019/2/27
 */
public class VertxComponentLoaderTest {


    @Test
    public void executeLoadTest() {

        DefaultContainer container = new DefaultContainer();
        VertxComponentLoader loader = new VertxComponentLoader(container);
        container.start("me.wang007");

        Vertx vertx = Vertx.vertx();
        vertx.registerVerticleFactory(new StartVerticleFactory());
        loader.executeLoad(container, vertx);
    }

}
