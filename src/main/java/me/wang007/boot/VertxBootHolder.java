package me.wang007.boot;


/**
 * VertxBoot实例的载体
 *
 * created by wang007 on 2019/2/27
 */
public class VertxBootHolder {

    public static volatile VertxBoot vertxBoot;

    static synchronized void setVertxBoot(VertxBoot vertxBoot) {
        VertxBootHolder.vertxBoot = vertxBoot;
    }

    public static VertxBoot vertxBoot() {
        VertxBoot vb = vertxBoot;
        return vb;
    }

}
