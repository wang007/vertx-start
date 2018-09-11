package org.wang007.verticle;

import io.vertx.core.*;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CookieHandler;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.wang007.annotation.Inject;
import org.wang007.annotation.InjectToSuper;
import org.wang007.annotation.Route;
import org.wang007.init.Initial;
import org.wang007.ioc.InternalContainer;
import org.wang007.ioc.component.ComponentAndFieldsDescription;
import org.wang007.router.LoadRouter;
import org.wang007.router.delegate.DelegateRouter;
import org.wang007.router.delegate.RouteUtils;
import org.wang007.utils.StringUtils;


import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * httpServer. 启动httpServer.
 * <p>
 * 覆盖 {@link #addressAndPort()} 方法提供部署的端口
 * <p>
 * 覆盖{@link #doInit(Vertx, Context)} 做verticle的初始化工作
 * <p>
 * 覆盖{@link #before(Router)} 做一些部署全局router操作
 * <p>
 * 覆盖{@link #beforeAccept(HttpServerRequest)} 做接受请求前的前置操作， 区别于{@link #before(Router)}方法
 * <p>
 * 覆盖{@link #options()} 提供部署的参数
 * <p>
 * 覆盖{@link #deployedHandler()} 做部署完成之后的操作
 * <p>
 * 覆盖{@link #doStop(HttpServer)} 做undeploy之后的操作
 * <p>
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
        if (first) logger.info("prepare to start httpServer. in {}", name);
        doInit(vertx, context);
    }

    /**
     * @param vertx
     * @param context
     */
    protected void doInit(Vertx vertx, Context context) {
        //NOOP
    }


    /**
     * 启动httpServer的操作
     * <p>
     * 在这里，可以做全局的router设置。 例如， {@link BodyHandler}, {@link CookieHandler} 等一些全局性的过滤
     *
     * @param mainRouter 主路由器
     */
    protected void before(Router mainRouter) {
        //NOOP
    }

    @Override
    public final void start(Future<Void> startFuture) throws Exception {
        long start = System.currentTimeMillis();
        Router mainRouter = Router.router(vertx);
        try {
            before(mainRouter);
        } catch (Exception e) {
            logger.error("execute before failed.", e);
            throw e;
        }

        Map<String, Router> subRouters = new HashMap<>();   //挂载的子路由
        List<? extends ComponentAndFieldsDescription> loadRouters = container.loadRouters();
        List<LoadRouterTuple> tuples = new ArrayList<>(loadRouters.size());
        loadRouters.forEach(lr -> {
            LoadRouter instance = (LoadRouter) container.newInstanceAndInject(lr);
            tuples.add(new LoadRouterTuple(lr, instance));
        });
        tuples.stream().sorted((t1, t2) -> {
            int order1 = t1.instance.order();
            int order2 = t2.instance.order();
            if (order1 >= order2) return 1;
            else return -1;
        }).forEach(tuple -> {
            ComponentAndFieldsDescription cd = tuple.cd;
            LoadRouter instance = tuple.instance;
            Route route = (Route) cd.annotation;
            String prefix = RouteUtils.checkPath(route.value());
            String mountPath = RouteUtils.checkPath(route.mountPath());
            boolean shared = route.sharedMount();

            Router subRouter = null;
            if (!StringUtils.isEmpty(mountPath)) {   //需要挂载
                if (shared) {
                    subRouter = subRouters.computeIfAbsent(mountPath, k -> {
                        Router subRouter0 = Router.router(vertx);
                        mainRouter.mountSubRouter(mountPath, subRouter0);
                        return subRouter0;
                    });
                } else {
                    subRouter = Router.router(vertx);
                    mainRouter.mountSubRouter(mountPath, subRouter);
                }
            }
            DelegateRouter delegate = new DelegateRouter(subRouter != null ? subRouter : mainRouter);
            if (!StringUtils.isEmpty(prefix)) delegate.setPathPrefix(prefix).setMountPath(mountPath);
            if (instance instanceof Initial) {
                Initial init = (Initial) instance;
                init.initial(vertx);
            }
            instance.init(delegate, vertx);
            instance.start(delegate, vertx);
        });

        AddressAndPort info = addressAndPort();
        server = vertx.createHttpServer().requestHandler(request -> {
            boolean success;
            try {
                success = beforeAccept(request);
            } catch (Exception e) {
                logger.error("beforeAccept handle failed.", e);
                request.response().setStatusCode(500).setStatusMessage("server failed").end();
                return;
            }
            if (success) mainRouter.accept(request);
        }).listen(info.port, info.address);

        if (first) pathLog(mainRouter, subRouters);

        logger.info("{} deployed successful. ", name);
        if (first) {
            long end = System.currentTimeMillis();
            logger.info("http server started successful. listen in {}. ", info.port);
            logger.info("http server deploy time: {}ms", (end -start));
        }
        startFuture.complete();
    }

    @Override
    public final void stop(Future<Void> stopFuture) throws Exception {
        doStop(server);
        super.stop(stopFuture);
    }

    protected void doStop(HttpServer server) {
        //NOOP
    }


    /**
     * 请求到来时，执行此方法
     * <p>
     * 做前置 request, response处理
     *
     * @param request req
     * @return true: 继续做处理，  false：结束处理。
     */
    protected boolean beforeAccept(HttpServerRequest request) {
        return true;
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


    private static void pathLog(Router mainRouter, Map<String, Router> subRouters) {
        StringBuilder log = new StringBuilder(2048).append("\r\n");
        log.append("------------ Main-Router all path ----------------").append("\r\n");
        mainRouter.getRoutes().forEach(route -> {
            String path = route.getPath();
            if(!subRouters.containsKey(path)) {
                log.append(path).append("\r\n");
            }

        });
        log.append("\r\n");
        subRouters.forEach((mountPath, subRouter) -> {
            log.append("---------- Sub-Router:"+ mountPath +". all path-----------").append("\r\n");
            subRouter.getRoutes().forEach(route -> {
                String path = route.getPath();
                log.append(mountPath).append(path).append("\r\n");
            });
            log.append("\r\n");
        });
        logger.info(log.toString());
    }



    static class LoadRouterTuple {
        public final ComponentAndFieldsDescription cd;
        public final LoadRouter instance;

        public LoadRouterTuple(ComponentAndFieldsDescription cd, LoadRouter instance) {
            this.cd = cd;
            this.instance = instance;
        }
    }


}
