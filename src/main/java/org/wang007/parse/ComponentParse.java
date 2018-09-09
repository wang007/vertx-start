package org.wang007.parse;

import io.vertx.core.Vertx;
import org.wang007.annotation.root.RootForComponent;
import org.wang007.ioc.ComponentFactory;
import org.wang007.ioc.component.ComponentAndFieldsDescription;
import org.wang007.ioc.component.ComponentDescription;

import java.util.List;
import java.util.Map;

/**
 * created by wang007 on 2018/8/23
 */
public interface ComponentParse {


    static ComponentParse create() {
        return new ComponentParseImpl();
    }


    /**
     * 解析出属性文件中属性
     *
     * 属性文件必须来自于classPath
     *
     * @param classpathPropertyFilePath classPath下的配置属性名
     * @return immutableMap
     */
    Map<String, String> parseProperties(String classpathPropertyFilePath);


    /**
     * 扫描被{@link RootForComponent}元注解的注解的class
     *
     * @param basePaths 基于该路径扫描所有的class
     * @param vertx
     * @return
     */
    List<? extends ComponentDescription> parseClassFromRoot(Vertx vertx, String... basePaths);

    /**
     * 扫描出组件中需要注入的属性
     * @param cds
     * @return
     */
    List<? extends ComponentAndFieldsDescription> parseFieldsForComponents(List<? extends ComponentDescription> cds);

    /**
     * 创建实例
     * @param cd 组件描述
     * @return 根据组件描述创建的实例
     */
    <T extends ComponentAndFieldsDescription> Object newInstance(T cd);

    /**
     * 根据class生成组件描述。
     *
     * @param clz
     * @param <T>
     * @return 组件描述 or null
     */
    <T extends ComponentAndFieldsDescription> T createComponent(Class<?> clz);

    /**
     * 替换掉默认的{@link ComponentFactory#create()}
     *
     * @param factory
     * @return this
     */
    ComponentParse setComponentFactory(ComponentFactory factory);


}
