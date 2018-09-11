package org.wang007.examples;

import io.vertx.core.Vertx;
import org.wang007.boot.VertxBoot;
import org.wang007.ioc.Container;

import java.util.Arrays;

/**
 * created by wang007 on 2018/9/10
 */
public class Main {

    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory");
        Vertx vertx = Vertx.vertx();
        VertxBoot boot = VertxBoot.create(vertx);
        boot.setBasePaths(Arrays.asList("org.wang007"));
        boot.start();
        long end = System.currentTimeMillis();
        System.out.println("time: -> " +(end -start) + "ms");

        Container container = boot.getContainer();
        System.out.println(container);

        //bug 1. 把Main这个类不该有的，也扫进来了。 kill
        //    2. 没有把所有的父类class找完全。
        //    3. fuck sendable的实现功亏一篑

    }
}
