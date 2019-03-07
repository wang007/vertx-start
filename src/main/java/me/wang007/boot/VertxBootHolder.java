package me.wang007.boot;


import io.vertx.core.Verticle;
import me.wang007.container.LoadContainer;
import me.wang007.router.LoadRouter;

/**
 * VertxBoot实例的载体
 *
 * 在{@link LoadContainer#start(String...)}调用中，进行设置值
 *
 * created by wang007 on 2019/2/27
 */
public class VertxBootHolder {

    private static volatile VertxBoot vertxBoot;

    /**
     * 在{@link VertxBoot#start()}方法中调用 在部署{@link Verticle}和加载{@link LoadRouter}之前
     *
     */
    static synchronized void setVertxBoot(VertxBoot vertxBoot) {
        VertxBootHolder.vertxBoot = vertxBoot;
    }

    /**
     * 可在以下方式中获取{@link VertxBoot}实例。
     *
     * 1.{@link Verticle}的生命周期方法 或者普通的static代码块中
     * 2.{@link LoadRouter}的生命周期方法 或者普通的static代码块中
     *
     * 其他方式，得确保在{@link LoadContainer#start(String...)}方法调用之后再获取
     *
     * @return vertxBoot实例  or null
     */
    public static VertxBoot vertxBoot() {
        VertxBoot vb = vertxBoot;
        return vb;
    }

}
