package me.wang007.annotation;


import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import me.wang007.router.LoadRouter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * vertx-web中， 使用该注解用于扫描{@link LoadRouter}
 * <p>
 * 注意：该注解只能使用到{@link LoadRouter}
 *
 * 如果有同时设置{@link #value()} 和 {@link #mountPath()}
 * 那么最终的路径是 mountPath + value + {@link io.vertx.ext.web.Route#getPath()}
 *
 * <code>
 *  @Route("/person")
 *  class PersonRouter implements LoadRouter {
 *
 *
 *     public void start(Router router, Vertx vertx) {
 *
 *          router.get("/:id").handler(rc -> {
 *
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
public @interface Route {

    /**
     * 请求路径的前缀
     * 默认是 "", 即没有前缀， 直接匹配{@link io.vertx.ext.web.Route#path(String)}
     * 如果设置{@code value()} 请求路径： value() + {@link io.vertx.ext.web.Route#getPath()}
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

}
