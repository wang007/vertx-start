package org.wang007.boot;

import io.vertx.core.Vertx;
import org.wang007.ioc.Container;

import java.util.List;

/**
 * vertx-start快速启动器
 *
 * created by wang007 on 2018/8/31
 */
public interface VertxBoot {


    static VertxBoot create(Vertx vertx) {
        return new VertxBootImpl(vertx);
    }

    /**
     * 设置配置文件的路径， 用于读取属性
     *
     * 默认就是 application.properties
     *
     * @param configFilePath classpath下的配置文件路径  如果在目录内，请务必带上目录。
     *
     * @return
     */
    VertxBoot setConfigFilePath(String configFilePath);

    /**
     *
     * @param basePaths
     * @return
     */
    VertxBoot setBasePaths(List<String> basePaths);


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
