package org.wang007.boot;

import io.vertx.core.*;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.wang007.annotation.Deploy;
import org.wang007.constant.PropertyConst;
import org.wang007.exception.InitialException;
import org.wang007.exception.VertxStartException;
import org.wang007.ioc.Container;
import org.wang007.ioc.InternalContainer;
import org.wang007.ioc.component.ComponentAndFieldsDescription;
import org.wang007.ioc.impl.DefaultContainer;
import org.wang007.json.JsonArraySend;
import org.wang007.json.JsonSend;
import org.wang007.json.codec.JsonArraySendMessageCodec;
import org.wang007.json.codec.JsonSendMessageCodec;
import org.wang007.parse.ComponentParse;
import org.wang007.parse.ComponentParseImpl;
import org.wang007.utils.CollectionUtils;
import org.wang007.utils.StringUtils;
import org.wang007.verticle.StartVerticleFactory;
import org.wang007.verticle.VerticleConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.wang007.verticle.StartVerticleFactory.Start_Prefix;

/**
 * created by wang007 on 2018/9/9
 */
public class VertxBootImpl implements VertxBoot {

    private static final Logger logger = LoggerFactory.getLogger(VertxBootImpl.class);

    private Vertx vertx;

    private String configFilePath = PropertyConst.Default_Properties_Path;

    private boolean start = false;

    private List<String> basePaths = new ArrayList<>();

    private InternalContainer container;


    protected void assertNotStart() {
        if (start) throw new InitialException("Vertx boot already start.");
    }

    protected void assertStart() {
        if (!start) throw new InitialException("vertx boot not start. make sure called #start method.");
    }


    public VertxBootImpl(Vertx vertx) {
        this.vertx = vertx;
        this.container = new DefaultContainer(vertx);
    }

    @Override
    public VertxBoot setConfigFilePath(String configFilePath) {
        Objects.requireNonNull(configFilePath, "required configFilePath.");
        assertNotStart();
        this.configFilePath = configFilePath;
        return this;
    }

    @Override
    public VertxBoot setBasePaths(List<String> basePaths) {
        Objects.requireNonNull(basePaths, "required basePaths.");
        assertNotStart();
        this.basePaths = basePaths;
        return this;
    }

    @Override
    public Container getContainer() {
        assertStart();
        return container;
    }

    @Override
    public synchronized void start() {
        assertNotStart();
        ComponentParse parse = new ComponentParseImpl();
        init(vertx, container, parse);

        List<? extends ComponentAndFieldsDescription> verticles = container.verticles();
        List<VerticleTuple> tuples = new ArrayList<>(verticles.size());
        verticles.forEach(v -> {
            Verticle instance = (Verticle) parse.newInstance(v);
            tuples.add(new VerticleTuple(v, instance));
        });
        logger.info("prepare deploy all verticle...");
        tuples.stream().sorted((t1, t2) -> {
            Deploy d1 = (Deploy) t1.cd.annotation;
            Deploy d2 = (Deploy) t2.cd.annotation;
            int order1 = d1.order();
            int order2 = d2.order();
            if(order1 >= order2) return 1;
            else return -1;
        }).forEach(t -> {
            String verticleName = t.cd.clazz.getName();
            logger.info("deploy verticle -> {}", verticleName);

            Deploy deploy = (Deploy) t.cd.annotation;
            Verticle instance = t.instance;
            VerticleConfig config = instance instanceof VerticleConfig ? (VerticleConfig) instance: null;
            DeploymentOptions options = config != null ? config.options(): new DeploymentOptions();
            if(options == null) throw new VertxStartException(t.cd.clazz + " #options() returned null");

            boolean requireSingle = config != null && config.requireSingle();
            int instanceCount = deploy.instances();
            boolean worker = deploy.worker();
            boolean multiWork = deploy.multiThreaded();

            if(instanceCount != DeploymentOptions.DEFAULT_INSTANCES) options.setInstances(instanceCount);
            if(worker != DeploymentOptions.DEFAULT_WORKER) options.setWorker(worker);
            if(multiWork != DeploymentOptions.DEFAULT_MULTI_THREADED) options.setMultiThreaded(multiWork);
            if(requireSingle && options.getInstances() != 1) throw new IllegalStateException("verticleName must be single instance");

            Handler<AsyncResult<String>> deployedHandler = config != null ? config.deployedHandler(): null;
            vertx.deployVerticle(Start_Prefix + ':' + verticleName, options, deployedHandler);
        });
        start = true;
    }

    /**
     * @param vertx
     */
    private void init(Vertx vertx, InternalContainer container, ComponentParse parse) {
        EventBus eventBus = vertx.eventBus();
        logger.info("register JsonSend, JsonArraySend MessageCodec.");
        try {
            eventBus.registerDefaultCodec(JsonSend.class, new JsonSendMessageCodec());
        } catch (IllegalStateException e) {
            //ignore
        }
        try {
            eventBus.registerDefaultCodec(JsonArraySend.class, new JsonArraySendMessageCodec());
        } catch (IllegalStateException e) {
            //ignore
        }
        vertx.registerVerticleFactory(new StartVerticleFactory(container));

        container.appendComponent(vertx);
        container.appendComponent(eventBus);
        container.appendComponent(vertx.fileSystem());
        container.appendComponent(vertx.sharedData());
        container.appendComponent(container);

        logger.info("parse properties. configFilePath -> {}", configFilePath);
        Map<String, String> properties = parse.parseProperties(configFilePath);

        String paths = System.getProperty(PropertyConst.Default_Base_Path_Key);
        paths = StringUtils.trimToEmpty(paths);

        if (StringUtils.isEmpty(paths)) {
            logger.info("system properties not found basePaths.");
            paths = properties.get(PropertyConst.Default_Base_Path_Key);
        }
        if (StringUtils.isNotEmpty(paths)) {
            logger.info("found basePaths in config files.");
            String[] split = paths.split(",");
            if (split.length > 1) this.basePaths.clear();
            for (String path : split) {
                path = StringUtils.trimToEmpty(path);
                if (StringUtils.isNotEmpty(path)) basePaths.add(path);
            }
        } else {
            logger.warn("system properties and config file not found basePaths. in addition to set basePaths by vertxBoot #setBasePaths() method.");
        }
        if (CollectionUtils.isEmpty(basePaths)) throw new VertxStartException("not found basePaths");

        logger.info("calling container #appendProperties and #initail method.");
        container.appendProperties(properties); //往容器中添加属性
        container.initial(vertx, basePaths);    //初始化容器
        doInit(vertx,  container, parse);
    }

    /**
     * 留给子类实现
     *
     * @param vertx
     */
    protected void doInit(Vertx vertx, InternalContainer container, ComponentParse parse) {}


    static class VerticleTuple {
        public final ComponentAndFieldsDescription cd;
        public final Verticle instance;

        public VerticleTuple(ComponentAndFieldsDescription cd, Verticle instance) {
            this.cd = cd;
            this.instance = instance;
        }
    }

}
