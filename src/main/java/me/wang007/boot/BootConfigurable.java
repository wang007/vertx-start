package me.wang007.boot;

import me.wang007.container.Component;

/**
 * 配置VertxBoot读取的配置文件
 * 配置VertxBoot加载{@link Component}的基路径
 *
 * created by wang007 on 2019/3/14
 */
public interface BootConfigurable extends VertxBoot {

    /**
     * 设置配置文件的路径， 用于读取属性
     *
     * 默认就是 application.properties
     *
     * @param configFilePath classpath下的配置文件路径  如果在目录内，请务必带上目录。
     *
     * @return
     */
    BootConfigurable setConfigFilePath(String configFilePath);

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
    BootConfigurable setBasePaths(String... basePaths);
}
