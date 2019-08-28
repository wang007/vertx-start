package me.wang007.boot;

import io.vertx.core.*;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import me.wang007.annotation.Deploy;
import me.wang007.container.Component;
import me.wang007.container.Container;
import me.wang007.container.LoadContainer;
import me.wang007.exception.ErrorUsedAnnotationException;
import me.wang007.annotation.Route;
import me.wang007.exception.VertxStartException;
import me.wang007.router.LoadRouter;
import me.wang007.verticle.VerticleConfig;

import java.util.List;

import static me.wang007.verticle.StartVerticleFactory.Start_Prefix;

/**
 * 加载vert.x相关的组件， 例如：{@link Verticle}, {@link LoadRouter}
 *
 * created by wang007 on 2019/2/26
 */
public class VertxComponentLoader {

    private static final Logger logger = LoggerFactory.getLogger(VertxComponentLoader.class);


    public VertxComponentLoader(LoadContainer container) {
        if (container.started()) throw new IllegalStateException("Load container must be not started");
        container.registerLoadBy(Deploy.class).registerLoadBy(Route.class);
    }

    /**
     * 从容器中获取被{@link Deploy}注解的{@link Verticle}组件，并执行部署操作。
     *
     * @param container 组件容器
     * @param vertx
     */
    public void executeDeploy(Container container, Vertx vertx) {
        List<Component> components = container.getComponentsByAnnotation(Deploy.class);
        components.stream()
                .filter(c -> {
                    Deploy deploy = c.getAnnotation(Deploy.class);
                    if (deploy == null) {
                        logger.warn("component: {} not found @Deploy Annotation");
                        return false;
                    }
                    if (!(Verticle.class.isAssignableFrom(c.getClazz()))) {
                        throw new ErrorUsedAnnotationException("@Deploy can only be used on Verticle, component:" + c.getClazz().getName());
                    }
                    return true;
                })
                .sorted((c1, c2) -> {
                    Deploy d1 = c1.getAnnotation(Deploy.class);
                    Deploy d2 = c2.getAnnotation(Deploy.class);
                    int order1 = d1.order();
                    int order2 = d2.order();
                    if (order1 >= order2) return 1;
                    else return -1;
                })
                .forEach(component -> {
                    String verticleName = component.getClazz().getName();
                    logger.info("deploy verticle -> {}", verticleName);

                    Deploy deploy = component.getAnnotation(Deploy.class);
                    Verticle instance ;
                    try {
                        instance = (Verticle) component.getClazz().newInstance();
                    } catch (Exception e) {
                        throw new VertxStartException("create verticle instance failed, verticle: " + component.getClazz().getName(), e);
                    }

                    VerticleConfig config = instance instanceof VerticleConfig ? (VerticleConfig) instance: null;
                    DeploymentOptions options = config != null ? config.options(): new DeploymentOptions();
                    if(options == null) throw new VertxStartException(component.getClazz().getName() + " #options() returned null");

                    boolean requireSingle = config != null && config.requireSingle();

                    //verticle实例数
                    int instanceCount;
                    if(deploy.instances() == Integer.MAX_VALUE) instanceCount = VertxOptions.DEFAULT_EVENT_LOOP_POOL_SIZE;
                    else if(deploy.instances() == Integer.MAX_VALUE -2) instanceCount = VertxOptions.DEFAULT_EVENT_LOOP_POOL_SIZE /2;
                    else instanceCount = deploy.instances();

                    boolean worker = deploy.worker();

                    if(instanceCount != DeploymentOptions.DEFAULT_INSTANCES) options.setInstances(instanceCount);
                    if(worker != DeploymentOptions.DEFAULT_WORKER) options.setWorker(worker);
                    if(requireSingle && options.getInstances() != 1) throw new IllegalStateException("verticleName must be single instance");

                    Handler<AsyncResult<String>> deployedHandler = config != null ? config.deployedHandler(): null;
                    //部署完成提示
                    Handler<AsyncResult<String>> delegateHandler = ar -> {
                        if(ar.succeeded()) {
                            logger.info(" {} deployed successfully.", verticleName);
                        } else {
                            logger.error(" !!!!=====> "+ verticleName +" deployed failed, please check it and restart.");
                            logger.error("", ar.cause());
                            logger.error("verticle deployment failed, please restart...");
                        }
                        if(deployedHandler != null) deployedHandler.handle(ar);
                    };
                    vertx.deployVerticle(Start_Prefix + ':' + verticleName, options, delegateHandler);
                });
    }


}
