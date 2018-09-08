package org.wang007.ioc;

import io.vertx.core.Vertx;
import org.wang007.ioc.component.ComponentAndFieldsDescription;
import org.wang007.ioc.impl.ComponentDescrCollections;
import org.wang007.parse.ComponentParse;

import java.util.List;
import java.util.Map;

/**
 * 仅供内部使用的ioc接口。
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
     * @param instances 实例集合
     */
    void appendComponents(List<Object> instances);

    /**
     * 在完成容器初始化之前， 添加属性
     *
     * @param properties
     */
    void appendProperties(Map<String, String> properties);



}
