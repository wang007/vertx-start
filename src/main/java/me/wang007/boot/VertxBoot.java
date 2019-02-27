package me.wang007.boot;

import io.vertx.core.Vertx;
import me.wang007.container.Component;
import me.wang007.container.Container;

import java.util.Map;


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
     * 设置加载 {@link Component}的基路径
     *
     * 默认：
     *  1. 如果配置文件中有设置的话，就从配置文件中读取。
     *  2. 读取调用者所在类的路径
     *
     * @param basePaths
     * @return
     */
    VertxBoot setBasePaths(String... basePaths);


    /**
     * 获取一个ioc容器
     * @return
     * @throws
     */
    Container getContainer();

    /**
     * 获取配置文件中加载好的属性
     *
     * @param key 配置文件的key
     */
    String getProperty(String key);

    /**
     * 获取所有配置文件中加载好的属性
     */
    Map<String, String> getProperties();


    /**
     * 装载属性到指定的实体上
     *
     * @param propertiesClz 指定类，该类必须有{@link me.wang007.annotation.Properties} 注解
     * @param <E>
     * @return
     */
    <E> E loadFor(Class<E> propertiesClz);


    /**
     * 启动 vertx-start
     */
    void start();

}
