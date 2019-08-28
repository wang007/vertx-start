package me.wang007.example;

import io.vertx.core.Vertx;
import me.wang007.boot.VertxBoot;
import me.wang007.boot.VertxBootWithHook;

/**
 * created by wang007 on 2019/2/27
 */
public class Main {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        VertxBoot.create(vertx)
                .afterDeployedHook(VertxBootHolder::setVertxBoot)
                .start();
    }
    public static final class VertxBootHolder {
        public volatile static VertxBoot vertxBoot;
        public synchronized static void  setVertxBoot(VertxBoot boot) {
            vertxBoot = vertxBoot;
        }

        public static VertxBoot getVertxBoot() {
            return vertxBoot;
        }
    }
}
