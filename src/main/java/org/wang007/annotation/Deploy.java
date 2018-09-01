package org.wang007.annotation;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Verticle;
import org.wang007.annotation.root.RootForComponent;
import org.wang007.verticle.VerticleConfig;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 被注解的{@link Verticle}是需要部署到vertx中的
 *
 * 该注解只能使用到Verticle中
 *
 * 注意：{@link Deploy} 注解的Verticle 不能作为 组件 注入到其他属性中
 *      允许其他组件注入到Verticle中
 *
 * <code>
 *     @Deploy
 *     public class DemoVerticle extends AbstractVerticle {
 *         @Inject
 *         private Test test;
 *
 *         public void start() {
 *         }
 *     }
 *
 *     ====================================
 *
 *     @Deploy(instances = 8, worker = true)
 *     public class DemoVerticle extends AbstractVerticle {
 *         @Inject
 *         private Test test;
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
 *         @Inject
 *         private Test test;
 *
 *         @Value("person.name")
 *         private String name;
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
@RootForComponent
public @interface Deploy {

    /**
     * 被部署的verticle 的实例数
     */
    int instances() default DeploymentOptions.DEFAULT_INSTANCES ;

    /**
     * 是否为worker Verticle
     */
    boolean worker() default DeploymentOptions.DEFAULT_WORKER ;

    /**
     * 是否为 multi Worker Verticle
     * @return
     */
    boolean multiThreaded() default DeploymentOptions.DEFAULT_MULTI_THREADED;

}
