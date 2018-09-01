package org.wang007.annotation;


import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import org.wang007.annotation.root.RootForComponent;
import org.wang007.router.LoadRouter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * vertx-web中， 使用该注解用于扫描{@link LoadRouter}
 * <p>
 * 注意：该注解只能使用到{@link LoadRouter}
 * <p>
 * 注意：被Route 注解的LoadRouter 不能作为组件， 注入到其他组件中
 *
 * <code>
 *  @Route("/person")
 *  class PersonRouter implements LoadRouter {
 *
 *     @Inject
 *     private PersonConfig config;
 *
 *     public void start(Router router, Vertx vertx) {
 *
 *          router.get("/:id").handler(rc -> {
 *              config.xxx
 *       });
 *
 *      router.post("/add").handler(rc -> {
 *
 *     });
 *     }
 *  }
 * </code>
 * <p>
 * Created by wang007 on 2018/8/21.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@RootForComponent
public @interface Route {

    /**
     * 请求路径的前缀
     * 默认是 "", 即没有前缀， 直接匹配{@link io.vertx.ext.web.Route#path(String)}
     * 如果设置{@code value()} 请求路径： value() + {@link io.vertx.ext.web.Route#path(String)}
     */
    String value() default "";

    /**
     * 即把{@link LoadRouter#start(Router, Vertx)}中的所有{@link io.vertx.ext.web.Route}挂载到{@code #mountPath()}
     * <p>
     * 如果{@code mountPath}, {@code monthPoint} 都有设置， 则只有{@code mountPath} 生效
     * <p>
     * 关于挂载，请参考vert.x的官方文档
     */
    String mountPath() default "";

    /**
     * 是否同一个httpserver内共享挂载点
     * @return true：共享挂载点，即共用一个subRouter   false: 每次都是创建一个新的
     */
    boolean sharedMount() default true;


    /**
     * 即把{@link LoadRouter#start(Router, Vertx)}中的所有{@link io.vertx.ext.web.Route}挂载到
     * 指定的{@link Router}下
     */
    Class<? extends Router> monthRouter() default Router.class;

}
