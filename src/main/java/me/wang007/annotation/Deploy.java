package me.wang007.annotation;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Verticle;
import me.wang007.verticle.VerticleConfig;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 被注解的{@link Verticle}是需要部署到vertx中的
 *
 * 该注解只能使用到Verticle中

 *
 * <code>
 *     @Deploy
 *     public class DemoVerticle extends AbstractVerticle {
 *         public void start() {
 *         }
 *     }
 *
 *     ====================================
 *
 *     @Deploy(instances = 8, worker = true)
 *     public class DemoVerticle extends AbstractVerticle {
 *         @Inject
 *         private Test container;
 *
 *         public void start() {
 *         }
 *     }
 *
 *     =====================================
 *
 *     //更多配置化的Verticle，可实现 {@link VerticleConfig}
 *
 *     @Deploy
 *     public class DemoVerticle extends AbstractVerticle implements VerticleConfig {
 *
 *         public void start() {
 *         }
 *
 *         DeploymentOptions options() {
 *             //这里返回 部署参数， 从而获取最灵活的部署
 *         }
 *
 *         Class<? extends Handler<AsyncResult<String>>> deployedHandlerClass() {
 *              //这里返回部署完成之后的handler class
 *         }
 *     }
 * </code>
 *
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Deploy {

    /**
     * 被部署的verticle 的实例数
     * 1. 实例数等于{@link Integer#MAX_VALUE}，那么就等于eventLoop实例数
     * 2. 实例数等于{@link Integer#MAX_VALUE -2}, 那么就等于eventLoop实例数的一半
     *
     *
     */
    int instances() default DeploymentOptions.DEFAULT_INSTANCES ;

    /**
     * 是否为worker Verticle
     */
    boolean worker() default DeploymentOptions.DEFAULT_WORKER ;

    /**
     * verticle部署时的顺序 值越小， 排越前面。
     */
    int order() default 0;

}
