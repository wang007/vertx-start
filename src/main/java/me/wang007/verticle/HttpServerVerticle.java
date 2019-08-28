package me.wang007.verticle;

import io.vertx.core.*;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CookieHandler;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import me.wang007.container.Component;
import me.wang007.container.Container;
import me.wang007.router.delegate.DelegateRouter;
import me.wang007.utils.SharedReference;
import me.wang007.annotation.Route;
import me.wang007.router.LoadRouter;
import me.wang007.router.delegate.RouteUtils;
import me.wang007.utils.StringUtils;


import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static me.wang007.constant.VertxBootConst.Key_Container;
import static me.wang007.constant.VertxBootConst.Key_Vertx_Start;

/**
 * httpServer. 启动httpServer.
 * <p>
 * 覆盖 {@link #addressAndPort()} 方法提供部署的端口
 * <p>
 * <p>
 * 覆盖{@link #before(Router)} 做一些部署全局router操作
 * <p>
 * 覆盖{@link #beforeAccept(HttpServerRequest)} 做接受请求前的前置操作， 区别于{@link #before(Router)}方法
 * <p>
 * 覆盖{@link #options()} 提供部署的参数
 * <p>
 * 覆盖{@link #deployedHandler()} 做部署完成之后的操作
 * <p>
 * created by wang007 on 2018/9/6
 */
public class HttpServerVerticle extends AbstractVerticle implements VerticleConfig {


    public <T extends HttpServerVerticle> T self() {
        return (T) this;
    }


    private static final Logger logger = LoggerFactory.getLogger(HttpServerVerticle.class);
    private static final String Http_Server_Name_Prefix = "httpServer-";

    private static final int Default_Listen_Port = 8080;

    private static AtomicInteger instanceCount = new AtomicInteger(0);

    protected final String name;  //
    private final boolean first;  // 是否 第一个httpServer实例

    {
        //初始化时， 先要new一个实例， 读取VerticleConfig中的信息， 所以除去第一次new， count是从 1开始的。
        int number = instanceCount.getAndIncrement();
        name = Http_Server_Name_Prefix + number;
        first = number == 1;
    }

    protected HttpServer server;

    @Override
    public final void init(Vertx vertx, Context context) {
        super.init(vertx, context);
        logger.debug("prepare to deploy {}", name);
        if (first) logger.info("prepare to start httpServer. in {}", name);
    }


    /**
     * 异步的方式执行init方法。
     * 子类在该方法初始化好一些client之后，{@link LoadRouter#init(Router, Vertx, HttpServerVerticle)}方法中获取
     *
     * @param initFuture
     */
    protected void init(Future<Void> initFuture) {
        initFuture.complete();
    }

    /**
     * 启动httpServer的操作
     * <p>
     * 在这里，可以做全局的router设置。 例如， {@link BodyHandler}, {@link CookieHandler} 等一些全局性的过滤
     *
     * @param mainRouter 主路由器
     */
    protected void before(Router mainRouter) {}


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

    @Override
    public final void start(Future<Void> startFuture) throws Exception {
        long start = System.currentTimeMillis();

        Future<Void> initF = Future.future();
        init(initF);

        Router mainRouter = Router.router(vertx);
        Map<String, Router> sharedSubRouters = new HashMap<>(); //共享挂载子路由
        Map<String, List<Router>> notSharedSubRouters = new HashMap<>(); //不共享挂载子路由

        Future.<Void>future(this::init)
                .compose(v -> {
                    try {
                        before(mainRouter);
                    } catch (Exception e) {
                        logger.error("execute before failed.", e);
                        return Future.failedFuture(e);
                    }
                    return Future.<Void>future();
                })
                .compose(v -> {
                    LocalMap<String, SharedReference<?>> map = vertx.sharedData().getLocalMap(Key_Vertx_Start);
                    @SuppressWarnings("unchecked")
                    SharedReference<Container> sharedRef = (SharedReference<Container>) map.get(Key_Container);
                    Container container = sharedRef.ref;

                    List<Component> components = container.getComponentsByAnnotation(Route.class);

                    List<LoadRouterTuple> tuples = new ArrayList<>(components.size());
                    for (Component c : components) {
                        LoadRouter instance;
                        try {
                            instance = (LoadRouter) c.getClazz().newInstance();
                        } catch (Exception e) {
                            logger.error(c.getComponentName() + "newInstance failed", e);
                            return Future.failedFuture(e);
                        }
                        tuples.add(new LoadRouterTuple(c, instance));
                    }

                    List<Future> futList = new ArrayList<>(tuples.size());

                    tuples.stream().sorted((t1, t2) -> {
                        int order1 = t1.instance.order();
                        int order2 = t2.instance.order();
                        return Integer.compare(order1, order2);
                    }).forEach(tuple -> {
                        Component component = tuple.component;
                        LoadRouter instance = tuple.instance;
                        Route route = component.getAnnotation(Route.class);
                        String prefix = RouteUtils.checkPath(route.value());
                        String mountPath = RouteUtils.checkPath(route.mountPath());
                        boolean shared = route.sharedMount();

                        Router subRouter = null;
                        if (!StringUtils.isEmpty(mountPath)) {   //需要挂载
                            if (shared) {
                                subRouter = sharedSubRouters.computeIfAbsent(mountPath, k -> {
                                    Router subRouter0 = Router.router(vertx);
                                    mainRouter.mountSubRouter(mountPath, subRouter0);
                                    return subRouter0;
                                });
                            } else {
                                subRouter = Router.router(vertx);
                                notSharedSubRouters.getOrDefault(mountPath, new ArrayList<>()).add(subRouter);
                                mainRouter.mountSubRouter(mountPath, subRouter);
                            }
                        }
                        DelegateRouter delegate = new DelegateRouter(subRouter != null ? subRouter : mainRouter);
                        if (!StringUtils.isEmpty(prefix)) delegate.setPathPrefix(prefix).setMountPath(mountPath);
                        instance.init(delegate, vertx, this);

                        Future<Void> future = Future.future();
                        futList.add(future);

                        instance.start(future);
                    });
                    return CompositeFuture.join(futList);
                })
                .compose(allAr -> {
                    if (allAr.failed()) {
                        logger.warn("HttpServerVerticle start failed.");
                        return Future.failedFuture(new VertxException("LoadRouter lifeCycle hooks failed", allAr.cause()));
                    }
                    AddressAndPort info = addressAndPort();

                    Future<Void> listenF = Future.future();
                    server = vertx.createHttpServer().requestHandler(request -> {
                        boolean success;
                        try {
                            success = beforeAccept(request);
                        } catch (Exception e) {
                            logger.error("beforeAccept handle failed.", e);
                            request.response().setStatusCode(500).setStatusMessage("server failed").end();
                            return;
                        }
                        if (success) mainRouter.handle(request);
                    }).listen(info.port, info.address, ar -> {
                        if (ar.failed()) {
                            logger.error("http server listen failed.", ar.cause());
                            listenF.fail(ar.cause());
                            return;
                        }
                        if (first) pathLog(mainRouter, sharedSubRouters, notSharedSubRouters);

                        if (first) {
                            long end = System.currentTimeMillis();
                            logger.info("http server started successful. listen in {}. ", info.port);
                            logger.info("http server deploy time: " + (end - start) + "ms");
                        }
                        listenF.complete();
                    });
                    return listenF;
                })
                .setHandler(ar -> {
                    if (ar.succeeded()) startFuture.complete();
                    else startFuture.fail(ar.cause());
                });


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


    private static void pathLog(Router mainRouter, Map<String, Router> sharedSubs, Map<String, List<Router>> notSharedSubs) {
        StringBuilder log = new StringBuilder(2048).append("\r\n");
        log.append("------------ Main-Router all paths ----------------").append("\r\n");
        mainRouter.getRoutes().forEach(route -> {
            String path = route.getPath();
            if (!sharedSubs.containsKey(path)) {
                log.append(path == null ? "/" : path).append("\r\n");
            }
        });
        log.append("\r\n");

        sharedSubs.forEach((mountPath, subRouter) -> {
            log.append("---------- Sub-Router:" + mountPath + ". all paths -----------").append("\r\n");
            subRouter.getRoutes().forEach(route -> {
                String path = route.getPath();
                log.append(mountPath).append(path).append("\r\n");
            });
            log.append("\r\n");
        });
        notSharedSubs.forEach((mountPath, notShareds) -> {
            log.append("----------not-shared Sub-Router:" + mountPath + ". all paths -----------").append("\r\n");
            notShareds.forEach(sub ->
                    sub.getRoutes().forEach(r -> {
                        String path = r.getPath();
                        log.append(mountPath).append(path).append("\r\n");
                    })
            );

        });
        logger.info(log.toString());
    }


    static class LoadRouterTuple {
        public final Component component;
        public final LoadRouter instance;

        public LoadRouterTuple(Component component, LoadRouter instance) {
            this.component = component;
            this.instance = instance;
        }
    }


}
