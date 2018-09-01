package org.wang007.test;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import org.wang007.annotation.Component;
import org.wang007.ioc.ComponentDefinition;

/**
 * created by wang007 on 2018/8/29
 */
@Component
public class DemoDefinition implements ComponentDefinition<Router> {

    @Override
    public Router supplyComponent(Vertx vertx) {
        return Router.router(vertx);
    }


}
