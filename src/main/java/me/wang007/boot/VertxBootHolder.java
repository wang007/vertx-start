package me.wang007.boot;


import io.vertx.core.Verticle;
import me.wang007.router.LoadRouter;

/**
 * VertxBoot实例的载体
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
     * 仅仅在{@link VertxBoot#start()}之后使用，或者在vert.x 组件生命周期内使用
     *
     * @return vertxBoot实例  or null
     */
    public static VertxBoot vertxBoot() {
        VertxBoot vb = vertxBoot;
        return vb;
    }

}
