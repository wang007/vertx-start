package me.wang007.boot;

import me.wang007.constant.VertxBootConst;

import java.util.Collections;
import java.util.List;

/**
 * created by wang007 on 2019/8/28
 */
public class BootOptions {

    /**
     * 启动时扫描基路径， 默认是VertxBoot启动时所在的路径，及其子路径
     */
    private List<String> basePath  = Collections.emptyList();

    /**
     * 配置文件路径 默认 application.properties.  只支持properties文件
     */
    private String configFilePath = VertxBootConst.Default_Properties_Path;

    public List<String> getBasePath() {
        return basePath;
    }

    public BootOptions setBasePath(List<String> basePath) {
        this.basePath = basePath;
        return this;
    }

    public String getConfigFilePath() {
        return configFilePath;
    }

    public BootOptions setConfigFilePath(String configFilePath) {
        this.configFilePath = configFilePath;
        return this;
    }
}
