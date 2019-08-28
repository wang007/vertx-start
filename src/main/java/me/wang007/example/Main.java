package me.wang007.example;

import io.vertx.core.Vertx;
import me.wang007.boot.VertxBoot;

/**
 * created by wang007 on 2019/2/27
 */
public class Main {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        VertxBoot boot = VertxBoot.create(vertx);
        boot.start();
    }

}
