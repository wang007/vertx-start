package org.wang007.verticle;

import io.vertx.core.*;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CookieHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wang007.annotation.Inject;
import org.wang007.annotation.InjectToSuper;
import org.wang007.ioc.InternalContainer;
import org.wang007.ioc.component.ComponentAndFieldsDescription;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * httpServer. 启动httpServer.
 *
 * 覆盖 {@link #addressAndPort()} 方法提供部署的端口
 *
 * 覆盖{@link #doInit(Vertx, Context)} 做verticle的初始化工作
 *
 * 覆盖{@link #before(Router)} 做一些部署全局router操作
 *
 * 覆盖{@link #beforeAccept(HttpServerRequest)} 做接受请求前的前置操作， 区别于{@link #before(Router)}方法
 *
 * 覆盖{@link #options()} 提供部署的参数
 *
 * 覆盖{@link #deployedHandler()} 做部署完成之后的操作
 *
 * 覆盖{@link #doStop(HttpServer)} 做undeploy之后的操作
 *
 * created by wang007 on 2018/9/6
 */
@InjectToSuper
public class HttpServerVerticle extends AbstractVerticle implements VerticleConfig {

    private static final Logger logger = LoggerFactory.getLogger(HttpServerVerticle.class);
    private static final String Http_Server_Name_Prefix = "httpServer-";

    protected static final int Default_Listen_Port = 8080;

    private static AtomicInteger instanceCount = new AtomicInteger(0);

    protected final String name;  //
    protected final boolean first;  // 是否 第一个httpServer实例
    {
        //初始化时， 先要new一个实例， 读取VerticleConfig中的信息， 所以除去第一次new， count是从 1开始的。
        int number = instanceCount.getAndIncrement();
        name = Http_Server_Name_Prefix + number;
        first = number == 1;
    }

    @Inject
    private InternalContainer container;    //IOC容器

    HttpServer server;


    @Override
    public final void init(Vertx vertx, Context context) {
        super.init(vertx, context);
        logger.debug("prepare to deploy {}", name);
        if(first) logger.info("prepare to start httpServer. in {}", name);

        doInit(vertx, context);
    }

    /**
     *
     * @param vertx
     * @param context
     */
    protected void doInit(Vertx vertx, Context context) {

    }


    /**
     * 启动httpServer的操作
     *
     * 在这里，可以做全局的router设置。 例如， {@link BodyHandler}, {@link CookieHandler} 等一些全局性的过滤
     *
     * @param mainRouter 主路由器
     */
    protected void before(Router mainRouter) {
        //NOOP
    }



    @Override
    public final void start(Future<Void> startFuture) throws Exception {

        Router router = Router.router(vertx);

        Map<String , Router> subRouter = new HashMap<>();   //挂载的

        List<? extends ComponentAndFieldsDescription> loadRouters = container.loadRouters();


        logger.info("{} deployed successful. ", name);
        if(first) logger.info("http server started successful. listen in {}, ", addressAndPort().port);
        startFuture.complete();
    }

    @Override
    public final void stop(Future<Void> stopFuture) throws Exception {

        server.close();

        super.stop(stopFuture);
    }

    protected void doStop(HttpServer server) {
        //NOOP
    }


    /**
     * 请求到来时，执行此方法
     *
     * 做前置 request, response处理
     *
     * @param request req
     * @return
     */
    protected void beforeAccept(HttpServerRequest request) {
        //NOOP
    }

    /**
     * http server监听的address port
     *
     * @return AddressAndPort
     */
    protected AddressAndPort addressAndPort() {
        return new AddressAndPort(Default_Listen_Port);
    }


    protected static class AddressAndPort {
        public final String address;
        public final int port;

        public AddressAndPort(String address, int port) {
            this.address = address;
            this.port = port;
        }

        public AddressAndPort(int port) {
            this.address = "0.0.0.0";
            this.port = port;
        }
    }

}
