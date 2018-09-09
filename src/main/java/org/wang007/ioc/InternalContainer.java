package org.wang007.ioc;

import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import org.wang007.init.Initial;
import org.wang007.ioc.component.ComponentAndFieldsDescription;
import org.wang007.ioc.impl.ComponentDescrCollections;
import org.wang007.parse.ComponentParse;
import org.wang007.router.LoadRouter;

import java.util.List;
import java.util.Map;

/**
 * 仅供内部使用的ioc接口。
 *
 * note: 请不要在外部环境使用该接口
 *
 * created by wang007 on 2018/8/29
 */
public interface InternalContainer extends Container {

    /**
     * 在容器中初始化component
     *
     * 分离出verticle，LoadRouter两类component组件
     *
     * 创建好之后单例的组件
     *
     * @param cds
     */
     void initComponents(List<? extends ComponentAndFieldsDescription> cds);

    /**
     * 获取loadRouter的组件集合
     *
     * note: 该方法必须在{@link #initComponents(List)} 之后调用，否则返回null
     *
     * @return loadRouters
     */
    List<? extends ComponentAndFieldsDescription> loadRouters();

    /**
     * 获取verticle的组件集合
     *
     * note: 该方法必须在{@link #initComponents(List)} 之后调用，否则返回null
     *
     * @return verticles
     */
    List<? extends ComponentAndFieldsDescription> verticles();

    /**
     * 设置 组件解析.
     *
     * 默认是 {@link ComponentParse#create()}
     *
     * @param parse
     */
    void setComponentParse(ComponentParse parse);


    /**
     * 完成容器的初始化
     * @param vertx
     */
    void initial(Vertx vertx);

    /**
     * 在完成容器的初始化之前，添加组件
     *
     * 可以重复调用
     *
     * @param instance 实例
     */
    InternalContainer appendComponents(Object instance);

    /**
     * 在完成容器初始化之前， 添加属性
     *
     * note: 会覆盖原有在配置文件中的属性
     *
     * @param properties
     */
    InternalContainer appendProperties(Map<String, String> properties);

    /**
     * 设置项目的基路径， 用于加载类
     *
     * @param basePaths
     * @return
     */
    InternalContainer setBasePaths(List<String> basePaths);

    /**
     * 创建实例，注入属性， 如果实现了{@link Initial}接口，调用{@link Initial#initial(Vertx)}方法
     *
     * 主要用于{@link LoadRouter} 和 {@link Verticle}的初始化
     *
     * note: 调用该方法不会组件加到属性中，只是完成初始化操作
     *
     * @param cd 组件描述，
     * @return 已经完成注入的实例
     */
    Object newInstanceAndInject(ComponentAndFieldsDescription cd);





}
