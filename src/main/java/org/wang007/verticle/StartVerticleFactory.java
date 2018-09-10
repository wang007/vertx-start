package org.wang007.verticle;

import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.spi.VerticleFactory;
import org.wang007.ioc.InternalContainer;
import org.wang007.ioc.component.ComponentAndFieldsDescription;
import org.wang007.parse.ComponentParse;


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

    private final InternalContainer container;

    public StartVerticleFactory(InternalContainer container){
        this.container = container;
    }

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
        ClassLoader loader = ComponentParse.defaultClassLoader();
        verticleName = VerticleFactory.removePrefix(verticleName);
        if(verticleName.endsWith(".java")) {
            throw new IllegalArgumentException("verticleName not support endWith java");
        }
        Class<?> clz = loader.loadClass(verticleName);
        for (ComponentAndFieldsDescription cd: container.verticles()) {
            if(cd.clazz == clz) return (Verticle) container.newInstanceAndInject(cd);
        }
        throw new Error("not found verticleName");
    }
}
