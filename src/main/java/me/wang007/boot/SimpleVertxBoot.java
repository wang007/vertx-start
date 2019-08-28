package me.wang007.boot;

import io.vertx.core.Vertx;
import io.vertx.core.shareddata.LocalMap;
import me.wang007.constant.VertxBootConst;
import me.wang007.container.Container;
import me.wang007.container.DefaultContainer;
import me.wang007.exception.InitialException;
import me.wang007.json.JsonArraySend;
import me.wang007.json.JsonSend;
import me.wang007.json.codec.JsonArraySendMessageCodec;
import me.wang007.json.codec.JsonSendMessageCodec;
import me.wang007.utils.SharedReference;
import me.wang007.utils.StringUtils;
import me.wang007.verticle.StartVerticleFactory;

import java.util.Map;
import java.util.function.Consumer;

/**
 * created by wang007 on 2019/8/28
 */
public class SimpleVertxBoot implements VertxBootWithHook {

    private final Vertx vertx;

    /**
     * 是否启动过的标记
     */
    private volatile boolean start = false;

    private final DefaultContainer container;   //组件容器

    private final PropertiesLoader prLoader ;   //属性加载器

    //hooks
    private Consumer<VertxBoot> beforeLoadComponentsHook;
    private Consumer<VertxBoot> afterLoadComponentsHook;
    private Consumer<VertxBoot> beforeDeployedHook;
    private Consumer<VertxBoot> afterDeployedHook;
    private Runnable afterStartHook;

    public SimpleVertxBoot(Vertx vertx) {
        this.vertx = vertx;
        this.container = new DefaultContainer();
        this.prLoader = new PropertiesLoader(container);
    }

    /**
     * @param vertx
     */
    private VertxBoot init(Vertx vertx, BootOptions options) {

        //设置vert.x相关
        vertx.registerVerticleFactory(new StartVerticleFactory());
        vertx.eventBus().registerDefaultCodec(JsonArraySend.class, new JsonArraySendMessageCodec());
        vertx.eventBus().registerDefaultCodec(JsonSend.class, new JsonSendMessageCodec());

        //将container, vertxBoot设置到SharedData中
        LocalMap<String, SharedReference<?>> startMap = vertx.sharedData().getLocalMap(VertxBootConst.Key_Vertx_Start);
        startMap.put(VertxBootConst.Key_Container, new SharedReference<>(container));
        startMap.put(VertxBootConst.Key_Vertx_Boot, new SharedReference<>(this));


        //加载配置文件中的属性
        String configFilePath = options.getConfigFilePath();
        if(StringUtils.isEmpty(configFilePath)) {
            logger.warn("未配置属性文件路径，不加载属性");
        } else {
            prLoader.loadProperties(configFilePath);
        }

        //用于加载vert.x相关的组件
        VertxComponentLoader vcl = new VertxComponentLoader(container);

        if(beforeLoadComponentsHook != null) beforeLoadComponentsHook.accept(this); //执行hook
        String[] basePathArr = new String[options.getBasePath().size()];
        options.getBasePath().toArray(basePathArr);
        container.start(basePathArr);     //启动容器，加载Component
        if(afterLoadComponentsHook != null) afterLoadComponentsHook.accept(this);  //执行hook

        if(beforeDeployedHook != null) beforeDeployedHook.accept(this); //执行hook
        vcl.executeDeploy(container, vertx);  //加载vert.x相关的组件
        if(afterDeployedHook != null) afterDeployedHook.accept(this);    //执行hook

        if(afterStartHook != null) afterStartHook.run();

        return this;
    }



    @Override
    public synchronized SimpleVertxBoot beforeLoadComponentsHook(Consumer<VertxBoot> hook) {
        this.beforeLoadComponentsHook = hook;
        return this;
    }

    @Override
    public synchronized SimpleVertxBoot afterLoadComponentsHook(Consumer<VertxBoot> hook) {
        this.afterLoadComponentsHook = hook;
        return this;
    }

    @Override
    public synchronized SimpleVertxBoot beforeDeployedHook(Consumer<VertxBoot> hook) {
        this.beforeDeployedHook = hook;
        return this;
    }

    @Override
    public synchronized SimpleVertxBoot afterDeployedHook(Consumer<VertxBoot> hook) {
        this.afterDeployedHook = hook;
        return this;
    }

    @Override
    public synchronized SimpleVertxBoot afterStartHook(Runnable hook) {
        this.afterStartHook = hook;
        return this;
    }


    @Override
    public Container getContainer() {
        return container;
    }

    @Override
    public String getProperty(String key) {
        return getProperties().get(key);
    }

    @Override
    public Map<String, String> getProperties() {
        return prLoader.getProperties();
    }

    @Override
    public <E> E loadFor(Class<E> propertiesClz) {
        return prLoader.loadFor(propertiesClz);
    }

    @Override
    public Vertx vertx() {
        return vertx;
    }


    @Override
    public VertxBoot start(BootOptions options) {
        assertNotStart();
        start = true;
        return init(vertx, options);
    }

    protected void assertNotStart() {
        if (start) throw new InitialException("Vertx boot already start.");
    }
}
