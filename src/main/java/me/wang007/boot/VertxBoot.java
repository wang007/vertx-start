package me.wang007.boot;

import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import me.wang007.constant.VertxBootConst;
import me.wang007.container.Container;
import me.wang007.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * vertx-start快速启动器
 *
 * created by wang007 on 2018/8/31
 */
public interface VertxBoot {

    Logger logger = LoggerFactory.getLogger(VertxBoot.class);

    static VertxBootWithHook create(Vertx vertx) {
        return new SimpleVertxBoot(vertx);
    }

    /**
     * 获取一个ioc容器
     * @return ioc容器
     */
    Container getContainer();

    /**
     * 获取配置文件中加载好的属性
     *
     * @param key 配置文件的key
     * @return 属性
     */
    String getProperty(String key);

    /**
     * 获取所有配置文件中加载好的属性
     * @return kv
     */
    Map<String, String> getProperties();


    /**
     * 装载属性到指定的实体上
     *
     * @param propertiesClz 指定类，该类必须有{@link me.wang007.annotation.Properties} 注解
     * @param <E> 类型
     * @return 指定的实体类型
     */
    <E> E loadFor(Class<E> propertiesClz);

    /**
     *
     * @return vertx实例
     */
    Vertx vertx();

    /**
     * 启动 vertx-start
     * @return this
     */
    default VertxBoot start() {

        BootOptions opt = new BootOptions();
        List<String> paths = new ArrayList<>();

        String value = System.getProperty(VertxBootConst.Default_Base_Path_Key);
        if(StringUtils.isNotEmpty(value)) {
            String[] split = value.split(",");
            for (String s : split) {
                if(StringUtils.isNotBlank(s)) paths.add(s);
            }
        } else {
            StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
            if (stacks.length >= 3) {
                //0. thread.getStackTrace 1. init  2.start  3.caller
                String name = stacks[2].getClassName();
                String packageName = name.substring(0, name.lastIndexOf("."));
                logger.info("default base paths -> {}", packageName);
                paths.add(packageName);
            }
        }

        opt.setBasePath(paths);
        return start(opt);
    }

    /**
     *
     * @param options 启动参数
     * @return this
     */
    VertxBoot start(BootOptions options);


}
