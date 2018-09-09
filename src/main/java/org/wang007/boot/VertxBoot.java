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
     * 设置配置文件的路径， 用于读取属性
     *
     * @param configFileName classpath下的配置文件名称  如果在目录内，请务必带上目录。
     *
     * @return
     */
    VertxBoot setConfigFileName(String configFileName);

    /**
     * 获取一个ioc容器
     * @return
     * @throws
     */
    Container getContainer();


    /**
     * 启动 vertx-start
     */
    void start();





}
