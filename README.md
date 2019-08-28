# vertx-start
## 简单地，快速地启动vert.x的手脚架
####vertx-start保留了vert.x原汁原味的开发方式，并没有修改运行时的任何东西。
#### vertx-start非常的轻量级，代码也就那么几行。而且只依赖了vertx-core，vertx-web。  是开发vert.x居家旅行、早日脱单的必备良药。 

### 有什么功能？
* @Route 
* @Deploy
* 免copy Json, JsonArray
* profiles.active


###快速启动vertx-start
```java
public class Main {
    public static void main(String[] args) {
	    Vertx vertx = Vertx.vertx();
	    VertxBoot boot = VertxBoot.create(vertx);
	    boot.start();
   }
}
```
 就是这么简单把vertx-start启动起来。 
这种启动方式确保你的配置文件中有base.paths属性。默认从启动类开始扫描。
 base.paths属性用于指定vertx-start扫描component（组件）的路径。 可以指定多个，用逗号","间隔。
 vertx-start是不管你的vertx怎么获取的，即是说，用Main方式启动也行，也用Launcher也可以。

#### 痛点1
大部分情况下，你的Router代码是在Verticle中组织的，当Route的数量少时还好，如果Route数量大的话，所有的Router代码组织到一个Verticle中，这是会让开发者很头痛的事情。
> 如果Router通过hack方式传到其他的Verticle中，其实这是没用的。Router最终运行所在的eventLoop是httpServer listen的那个Verticle。
> 这样会有很明显的“意识”问题，以为Router会在其他Verticle的上下文执行，其实不然。

#### vertx-start是如何解决这个痛点的呢？

把Router组织到LoadRouter上，按功能划分不同的功能的Router到LoadRouter中。
```java
@Route( mountPath = "/demo")
public class DemoRouter implements LoadRouter {
    @Override
    public void start(Router router, Vertx vertx) {
        router.route("/wang").handler(rc -> {
            rc.response().end("hello world");
        });
    }
}
```

```java
@Deploy(instances = 16)
public class HttpServer extends HttpServerVerticle {}
```
就通过这样简单的几行代码，就可以把httpServer启动起来。 
而instances是Verticle的实例数，HttpServer对应的Verticle实例数 = eventLoop count，充分发挥Vert.x的性能。

```java
public interface LoadRouter {
    /**
     * @param router 当使用{@link Route#mountPath()} 挂载路径， router为subRouter, (子路由)
     */
    void start(Router router, Vertx vertx);

    /**
     * @return 用于 {@link LoadRouter} 排序， 升序。 默认: 0.
     */
    default int order() {
        return 0 ;
    }

    /**
     * {@link LoadRouter}创建好后调用。
     * 例如：权限相关的route实现，可以放到该方法中。
     * @param router 路由器， 跟{@link #start(Router, Vertx)}中的是同一个router.
     */
    default void init(Router router, Vertx vertx) {}

}
```
* init方法在start方法之前执行。 例如一些前置Route（像权限校验的Route）可以在init方法创建。
* order方法用于LoadRouter实现类排序，order越小，越前面。意味着越先把LoadRouter中调用Route加到MainRouter容器中。
#### @Route怎么使用？
首先声明一点，@Route只能加到LoadRouter实现类上，否则报错。
##### @Route中的3个属性。
* value -> 路径前缀。默认是""，即没有路径前缀。像上面的实例代码， 最终的访问路径是 /demo/wang。即会把value的值拼接到Router定义route的路径中。
* mountPath -> 挂载路径。默认是""，即不挂载subRouter，直接挂载到MainRouter上。 先声明一点， mountPath跟value不冲突，如果两者同时存在，最终的访问路径是 /mountPath/value/path。
* sharedMount -> 是否共享挂载subRouter，默认是true。即共享。 绝大多数情况下，都是true。

>&nbsp;&nbsp;&nbsp;&nbsp;如果不熟悉vertx-web的话，会对这个挂载路径有疑问。 
>&nbsp;&nbsp;&nbsp;&nbsp;Router是Route的容器，里面有skipList保存了所有的Route。如果访问后面的Route的话，需要跟前面的Route逐一匹配。Route数量大的话，对性能有所损失。
>&nbsp;&nbsp;&nbsp;&nbsp;而mountPath就是Route进行了分类。把一些route加到subRouter上，最后把subRouter加到MainRouter。 更详细的解读，请参考vertx-web官方文档关于mount的解释。
>&nbsp;&nbsp;&nbsp;&nbsp;mountPath是强烈推荐使用一个属性。

#### 痛点2
当写完一个Verticle的时候，需要手动调用vertx#deployVerticle方法来部署Verticle。尽管你可能很不情愿，但是你必须这么做。
```java
@Deploy
public class DemoVerticle extends AbstractVerticle {
    @Override
    public void start() throws Exception {
        vertx.eventBus().<JsonSend>consumer("jsonSend", msg -> {
            System.out.println(msg.body());
            msg.reply(new JsonSend().put("name", "wang007"));
        });
    }
}
```
对，就是这么简单，通过@Deploy注解，就把Verticle部署了。

##### @Deploy的属性
 * instances -> 默认是1。Vertilce的实例数，这个没啥好说的了吧。
 * worker -> 默认是false。是否为workVerticle
 * multiThreaded -> 默认是false。是否为 multiWorkVerticle
 * order -> 默认是0。 部署Verticle时的顺序，值越小，越先部署。假如verticle之间有依赖的话，可以使用该属性。
 
> 也许你会说，这么属性还不够啊，vertx部署Verticle的时候，有很多属性可选呢， 甚至包括部署完成时的操作。 别急，都有， 听我娓娓道来。 

##### 实现VerticleConfig接口
```java
 public interface VerticleConfig {
    /**
     * 部署verticle的参数
     * {@link Deploy}中值 != 默认值 将会设置到options中
     * @return 部署参数
     */
    default DeploymentOptions options() {
        return new DeploymentOptions();
    }
    /**
     * 确保该verticle是单实例的 即{@link DeploymentOptions#instances} = 1
     * @return true: verticle必须单利，如果{@link Deploy#instances()} != 1 或 {@link #options()}中的instances != null 报错
     *         false: 允许多利的
     * @throws IllegalStateException
     */
    default boolean requireSingle() {
        return false ;
    }
    /**
     * 部署verticle完成之后的回调
     * {@link io.vertx.core.Vertx#deployVerticle(String, Handler)} 中的Handler
     * @return handler
     */
    default  Handler<AsyncResult<String>> deployedHandler() {
        return null;
    }
}
```
让verticle实现类实现VerticleConfig接口
* options方法，设置部署参数。如果@Deploy有设置数，且不等于默认参数。那么会设置到options中。
* requireSingle方法，确保Verticle单例。默认是false。如果设置返回true。且设置多实例数的话，报错。
* deployedHandler方法，设置部署完成后操作。默认是null。
#### HttpServerVerticle
> 是的，你没看错，要启动一个httpServer，必须继承HttpServerVerticle。并用@Deploy注解。骨灰级推荐**httpServer对应Verticle的实例数等于eventLoop的实例数**。才能充分发挥vert.x的性能。
> HttpServerVerticle有多个拓展方法。

1. **addressAndPort方法**。默认启动端口：8080，如果8080不合你的胃口。你只需要覆盖该方法，提供你的端口即可。
2. 调用doInit方法，实现启动Verticle时的init方法。
3. **before方法（敲黑板）**。传入的参数是MainRouter。在执行所有的LoadRouter方法之前执行，可以覆盖该方法，做一些全局的Route操作。 例如BodyHandler等。
4. doStop方法。传入的参数是httpServer（Vert.x中的）实例，做Verticle stop时的操作。
5. beforeAccept方法。传入的参数是request。在请求来临时，进入MainRouter之前执行。这一步可以做请求之前拦截操作。

#### 不知道算不算痛点的痛点3
> &nbsp;&nbsp;&nbsp;&nbsp;熟悉的vert.x的朋友，都知道。eventBus send json，jsonArray的时候，会发生一次copy操作。尽管你的代码中是能确保线程安全的。
> &nbsp;&nbsp;&nbsp;&nbsp;实现JsonSend，JsonArraySend， 大费周折，最后发现还是不够理想。 这里的不够理想是指send的时候必须要设置codecName。因为我的实现中走不到最后defaultCodecMap中。这个在实现之前没发现。瞎眼程序员。

#### JsonSend，JsonArraySend
```java
@Deploy
public class DemoVerticle extends AbstractVerticle {

    @Override
    public void start() throws Exception {
    
        vertx.eventBus().<JsonSend>consumer("jsonSend", msg -> {
            System.out.println(msg.body());
            msg.reply(new JsonSend().put("name", "wang007"));
        });
        
        vertx.eventBus().<JsonArraySend>consumer("jsonArraySend", msg -> {
            System.out.println(msg.body());
            msg.reply(new JsonArraySend().add("wang007"));
        });

        JsonSend json = new JsonSend().put("name", "wang007").put("hobby", "girl");
        
        vertx.eventBus().<JsonSend>send("jsonSend", json, JsonSend.options(), msg -> {
            System.out.println(msg.result().body());
        });
        
        JsonArraySend array = new JsonArraySend().add("xiaowang");
        
        vertx.eventBus().<JsonArraySend>send("jsonArraySend", array, JsonArraySend.options(), msg -> {
            System.out.println(msg.result().body());
        });
    }
}
```
其中JsonSend.options()， JsonArraySend.options()是必须的。 否则还是会发生copy。
就是说，你可以把JsonSend当成JsonObject来用，JsonArraySend当成JsonArray来用是没问题的。
#### JsonSend，JsonArraySend是如何实现的。
1.  &nbsp;&nbsp;其实关于JsonSend，JsonArraySend是一种妥协。
2.  &nbsp;&nbsp;JsonSend，JsonArraySend维护着一个属性，会自动判断是否调用了eventBus send。如果调用了JsonSend，JsonArraySend就变成不可变。同时这个不可变的json也传到Consumer中， consumer使用这个json也不可变。只能从里面读取数据。
3.  &nbsp;&nbsp;同时JsonSend，JsonArraySend的使用有一定的限制。例如不能存Map，List，Map可以用JsonObject代替，List可以用JsonArray。还有存进send中的JsonObject，JsonArray将的不可变。切记。尝试存的话，会报错。
4.  &nbsp;&nbsp;即是说JsonSend，JsonArraySend免copy的实现方式是通过send之后不可变实现的。
jsonSend，JsonArraySend没有100%不可变。但是正常使用是没问题的。还是那句话：你要做傻逼，没人拦得住你。

#### 简单的IOC。
vertx-start自实现了一个简单的IOC。有多简单呢？ 只支持单例。只支持属性注入。
> 作为我的观点呢。 其实vert.x是不需要IOC的。 但是我为什么要实现呢？
>  * 因为IOC容易破坏vert.x的线程隔离机制。
>  * 容易引进带状态的component在多个的eventLoop上执行，从来带来并发问题。 切记，切记，切记。 一定要保证component的线程安全问题。因为vertx-start没办法做有效的判断。
> ##### 好处呢？
>  * 就是IOC的那些功能
>  * 还有就是属性的读取。声明一个属性Component。 方便从配置文件中读取属性。
>  * 这个IOC非常的轻量，就是简单几个类的代码。因为重量级的IOC我也不会实现。
>  还是那句话， 你要做傻逼，没人能拦得住你。

#### IOC的使用
1.  &nbsp;&nbsp;&nbsp;&nbsp; LoadRouter，Verticle，Router不能作为Component，注入到其他Component，但是其他Component可以组件到LoadRouter，Verticle中。
2.   &nbsp;&nbsp;&nbsp;&nbsp;使用@Component注解，让该类成为一个Component。可以往这个Component注入Component或value。默认是那么是首字母小写的类名。可以用value属性覆盖。
3.   &nbsp;&nbsp;&nbsp;&nbsp;使用@ConfigurationProperties注解，让该类成为一个属性Component。
4.   &nbsp;&nbsp;&nbsp;&nbsp;使用@Inject注解注入Component，默认是按类型注入，如果指定value的话，按名称注入。
5.    &nbsp;&nbsp;&nbsp;&nbsp;使用@Value注解注入属性，必须指定名称。
4.   &nbsp;&nbsp;&nbsp;&nbsp;默认情况下，对父类的属性不做注入操作的。除非在父类中使用@InjectToSuper注解。
5.   &nbsp;&nbsp;&nbsp;&nbsp;实现Initial接口，可以做初始化操作。
6.   &nbsp;&nbsp;&nbsp;&nbsp;实现ComponentDefinition接口，覆盖supplyComponent方法，提供Component加到容器中。默认默认是那么是首字母小写的类名。可以用@Name注解supplyComponent方法进行覆盖。

示例代码
```java
//@Componet，@Inject, @Value演示。
@Component
public class JdbcClient {
	@Value("mysql.datasource.count")
	private int count;
	@Value("mysql.datasource.count")
	private Integer count;
    @Inject
    private DataComponent data;
    public void test() {
        System.out.println(data);
    }
}

//@ConfigurationProperties演示。 
//确保容器有这些属性
//mysql.datasource.username
//mysql.datasource.password
//mysql.datasource.count
//mysql.datasource.url
//mysql.datasource.shabi

@ConfigurationProperties(prefix = "mysql.datasource")
public class DataComponent {
    private String username;
    private String password;
    private int count;
    private String url;
    @Value("shabi")   //别名
    private String foolish;
    //省略getter，setter
}

//@InjectToSuper演示
@InjectToSuper
public class Parent {
    @Inject
    private JdbcClient client;
    @Value("mysql.datasource.count") //注意：没有${}
    private int count;
    public void test() {
        System.out.println("fuck you.shabi");
    }
}
@Component
public class Son extends Parent {
	private String xx;
}

//Initial接口的演示。
@Component
public class DemoInit implements Initial {
    @Override
    public void initial(Vertx vertx) {
        System.out.println("vertx -> " + vertx);
        System.out.println("init");
    }
}

//ComponentDefinition接口演示
@Component
public class JdbcClientSupply implements ComponentDefinition<JdbcClient> {
    @Override
    public JdbcClient supplyComponent(Vertx vertx) {
        System.out.println(vertx);
        return new JdbcClient();
    }
}
```
### 属性文件
>  1. &nbsp;vertx-start默认加载classpath下的application.properties文件。
>  2. &nbsp;可以调用VertxBoot #setConfigFilePath方法设置classpath下的其他路径
>  3. &nbsp;如果以上都不满足或者想要添加一些额外的属性， 可以在vertxBoot #start方法之前调用vertxboot #getContainer方法，然后强制成InternalContainer。 再调用appendProperties方法，添加属性。同样，也可以调用appendComponent方法来添加Component到容器中。
### 关于base.paths
- &nbsp;&nbsp;&nbsp;&nbsp;  可以添加多个path。
- &nbsp;&nbsp;&nbsp;&nbsp;  确保base.paths一定存在。 通过3种方式且有先后顺序。 先去- System属性文件中找（通过启动jvm的时候添加-Dbase.paths参数添加）。找不到再去属性文件中找， 找不到再去VertxBootx调用 setBasePaths中找。 以上3种方式找不到，报错。
- &nbsp;&nbsp;&nbsp;&nbsp;  base.paths就是组件的路径。确保注解的Component都包含在base.paths中
### 关于profiles.active
- &nbsp;&nbsp;&nbsp;&nbsp;  关于profiles.active，相信使用过spring的朋友都知道，用于指定不同环境的配置文件。
- &nbsp;&nbsp;&nbsp;&nbsp; 啰嗦一下，profiles.active文件的前缀、后缀必须跟主配置文件一样。
&nbsp;&nbsp;&nbsp;&nbsp; 例如：主配置文件：application.properties， profiles.active文件：application-dev.properties
- &nbsp;&nbsp;&nbsp;&nbsp;profiles.active加载方法且有先后顺序。先去System属性文件中找（通过启动jvm的时候添加-Dbase.paths参数添加）。找不到再去主属性文件中找。找不到就是没有。即不加载profiles.active文件。





###最后
####你用或不用，它都在那里，只增不减。 
## 真香.jpg