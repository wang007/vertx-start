package org.wang007.ioc;


import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import org.wang007.annotation.Deploy;
import org.wang007.annotation.Route;
import org.wang007.router.LoadRouter;

import java.util.List;
import java.util.Map;

/**
 * 简单的ioc容器  也就是一个垃圾的ioc。
 *
 * 特别注意：被{@link Route} 注解的{@link LoadRouter}和 被{@link Deploy} 注解的{@link Verticle} 不会加入容器保存起来。
 *            同时这两个也绝不允许注入到 其他组件中。
 *            但是允许 注入 组件到 属性中。
 *
 * 只支持将组件加到容器中， 注入到属性中。 其他特性统统没有。
 *
 * 为什么呢？ 因为vert.x对IOC的需求很弱，很弱。
 * 为什么很弱还有开发IOC呢。 因为我是傻逼啊。
 *
 * 其次通过ioc注入容易带来并发问题。
 *
 * 所以我在框架层面尽可能的做了避免。但还是那句话：你要做傻逼，没人能拦得住你。
 *
 * 反正这么说吧，在vertx中，谁用IOC，谁傻逼
 *
 * created by wang007 on 2018/8/22
 */
public interface Container {


    /**
     * 根据名称获取组件
     * @param name 组件名称 唯一标识
     * @param <T>  返回的类型
     * @return
     * @throws ClassCastException
     */
    <T> T getComponent(String name) throws ClassCastException ;

    /**
     * 根据名称获取组件
     *
     * @param name  组件名称 唯一标识
     * @param requireType 期待返回的类型
     * @param <T>
     * @return
     * @throws ClassCastException
     */
    <T> T getComponent(String name, Class<T> requireType) throws ClassCastException;

    /**
     * 根据类型获取组件
     * @param requireType
     * @param <T>
     * @return
     * @throws ClassCastException
     */
    <T> List<T> getComponent(Class<T> requireType) throws ClassCastException;


    String getProperty(String key);

    /**
     *
     * @return
     */
    Vertx vertx();

    /**
     * 返回集合。 集合不可变
     * @return
     */
    Map<String, String> getProperties();

}
