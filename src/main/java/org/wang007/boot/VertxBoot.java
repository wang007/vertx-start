package org.wang007.boot;

import io.vertx.core.Vertx;
import org.wang007.ioc.Container;

/**
 * created by wang007 on 2018/8/31
 */
public interface VertxBoot {


    static VertxBoot create(Vertx vertx) {
        return null;
    }

    /**
     *
     * @param configFileName classpath下的配置文件名称
     * @return
     */
    VertxBoot setConfigFileName(String configFileName);

    /**
     * 获取一个ioc容器
     * @return
     * @throws IllegalStateException 如果还
     */
    Container getContainer();


    /**
     * 启动 vertx-start
     */
    void start();





}
