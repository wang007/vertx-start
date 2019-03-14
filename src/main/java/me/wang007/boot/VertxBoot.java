package me.wang007.boot;

import io.vertx.core.Vertx;
import me.wang007.container.Container;

import java.util.Map;


/**
 * vertx-start快速启动器
 *
 * created by wang007 on 2018/8/31
 */
public interface VertxBoot {

    static VertxBootWithHook create(Vertx vertx) {
        return new VertxBootBuilder(vertx);
    }

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
     *
     * @return vertx实例
     */
    Vertx vertx();

    /**
     * 启动 vertx-start
     */
    VertxBoot start();

}
