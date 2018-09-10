package org.wang007.examples;

import io.vertx.core.Vertx;
import org.wang007.boot.VertxBoot;

import java.util.Arrays;

/**
 * created by wang007 on 2018/9/10
 */
public class Main {

    public static void main(String[] args) {

        Vertx vertx = Vertx.vertx();
        VertxBoot boot = VertxBoot.create(vertx);
        boot.setBasePaths(Arrays.asList("org.wang007.examples"));
        boot.start();

    }
}
