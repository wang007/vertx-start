package me.wang007.boot;

import io.vertx.core.*;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.shareddata.LocalMap;
import me.wang007.container.Container;
import me.wang007.exception.InitialException;
import me.wang007.json.JsonArraySend;
import me.wang007.utils.SharedReference;
import me.wang007.constant.PropertyConst;
import me.wang007.container.DefaultContainer;
import me.wang007.json.JsonSend;
import me.wang007.json.codec.JsonArraySendMessageCodec;
import me.wang007.json.codec.JsonSendMessageCodec;
import me.wang007.utils.StringUtils;
import me.wang007.verticle.StartVerticleFactory;


import java.util.*;


/**
 * created by wang007 on 2018/9/9
 */
public class VertxBootImpl implements VertxBoot {

    private static final Logger logger = LoggerFactory.getLogger(VertxBootImpl.class);

    private final Vertx vertx;

    private final List<String> basePaths = new ArrayList<>();   //组件基路径

    private String configFilePath = PropertyConst.Default_Properties_Path;  //配置文件的路径

    /**
     * 是否启动过的标记
     */
    private boolean start = false;


    private final DefaultContainer container;

    private final PropertiesLoader prLoader ;   //属性加载器

    private final Map<String, String> properties = new HashMap<>(); //配置属性


    protected void assertNotStart() {
        if (start) throw new InitialException("Vertx boot already start.");
    }

    protected void assertStart() {
        if (!start) throw new InitialException("vertx boot not start. make sure called #start method.");
    }

    public VertxBootImpl(Vertx vertx) {
        this.vertx = vertx;
        this.container = new DefaultContainer();
        prLoader = new PropertiesLoader(container);
    }

    @Override
    public synchronized VertxBoot setConfigFilePath(String configFilePath) {
        Objects.requireNonNull(configFilePath, "required configFilePath.");
        assertNotStart();
        this.configFilePath = configFilePath;
        return this;
    }

    @Override
    public synchronized VertxBoot setBasePaths(String... basePaths) {
        Objects.requireNonNull(basePaths, "required basePaths.");
        assertNotStart();
        this.basePaths.addAll(Arrays.asList(basePaths));
        return this;
    }

    @Override
    public Container getContainer() {
        assertStart();
        return container;
    }

    @Override
    public String getProperty(String key) {
        return properties.get(key);
    }

    @Override
    public Map<String, String> getProperties() {
        return Collections.unmodifiableMap(properties);
    }

    @Override
    public <E> E loadFor(Class<E> propertiesClz) {
        return prLoader.loadFor(propertiesClz, getProperties());
    }

    @Override
    public synchronized void start() {
        assertNotStart();
        init(vertx);
        start = true;
    }

    /**
     * @param vertx
     */
    private void init(Vertx vertx) {

        //设置vert.x相关
        vertx.registerVerticleFactory(new StartVerticleFactory());
        vertx.eventBus().registerDefaultCodec(JsonArraySend.class, new JsonArraySendMessageCodec());
        vertx.eventBus().registerDefaultCodec(JsonSend.class, new JsonSendMessageCodec());

        //加载配置文件中的属性
        prLoader.loadProperties(configFilePath).forEach(properties::put);

        //用于加载vert.x相关的组件
        VertxComponentLoader vcl = new VertxComponentLoader(container);

        VertxBootHolder.setVertxBoot(this);

        if (basePaths.size() == 0) {    //设置basePaths为调用者所在路径
            String value = properties.get(PropertyConst.Default_Base_Path_Key);
            if(value != null) {
                String[] split = value.split(",");
                for (String s : split) {
                    if(StringUtils.isNotBlank(s)) basePaths.add(s);
                }
            } else {
                StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
                if (stacks.length >= 4) {
                    //0. thread.getStackTrace 1. init  2.start  3.caller
                    String name = stacks[3].getClassName();
                    String packageName = name.substring(0, name.lastIndexOf("."));
                    logger.info("default base paths -> {}", packageName);
                    basePaths.add(packageName);
                }
            }
        }
        String[] paths = new String[basePaths.size()];
        basePaths.toArray(paths);
        container.start(paths);     //启动容器，加载Component

        //将container, vertxBoot设置到SharedData中
        LocalMap<String, SharedReference<?>> startMap = vertx.sharedData().getLocalMap(PropertyConst.Key_Vertx_Start);
        startMap.put(PropertyConst.Key_Container, new SharedReference<Container>(container));
        startMap.put(PropertyConst.Key_Vertx_Boot, new SharedReference<VertxBoot>(this));

        vcl.executeLoad(container, vertx);  //加载vert.x相关的组件
        doInit(vertx, container);
    }

    /**
     * 留给子类实现
     *
     * @param vertx
     */
    protected void doInit(Vertx vertx, Container container) {}

}
