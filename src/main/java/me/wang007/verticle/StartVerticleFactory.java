package me.wang007.verticle;

import io.vertx.core.Verticle;
import io.vertx.core.spi.VerticleFactory;
import me.wang007.container.Container;


/**
 * verticle factory, 覆盖默认的， 完成注入工作。
 *
 * created by wang007 on 2018/9/10
 */
public class StartVerticleFactory implements VerticleFactory {

    /**
     *
     */
    public static final String Start_Prefix = "start";

    @Override
    public boolean blockingCreate() {
        return true;
    }

    @Override
    public String prefix() {
        return Start_Prefix;
    }

    @Override
    public Verticle createVerticle(String verticleName, ClassLoader classLoader) throws Exception {
        //must be the same type classLoader
        //ComponentParseImpl #loadClass method
        ClassLoader loader = Container.Default_ClassLoader;
        verticleName = VerticleFactory.removePrefix(verticleName);
        if(verticleName.endsWith(".java")) {
            throw new IllegalArgumentException("verticleName not support endWith java");
        }
        Class<?> clz = loader.loadClass(verticleName);
        Object v = clz.newInstance();
        return (Verticle) v;
    }
}
