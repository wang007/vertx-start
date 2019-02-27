package me.wang007.constant;

/**
 * 自定义的application属性值
 *
 * created by wang007 on 2018/8/22
 */
public interface PropertyConst {

    /**
     * 默认项目基路径的key
     * 项目启动时  基于该路径搜索  扫描被注解的class
     *
     * 可以配置多个值  多值用 “,” 做分割
     *
     */
    String Default_Base_Path_Key = "base.path" ;

    /**
     * 多配置文件key
     * 例如： profiles.active =dev, 一并加载 application-dev.properties中的属性
     *       profiles.active =prod, 一并加载 application-prod.properties中的属性
     *
     * 如果 属性在application.properties, application-dev.properties 同时存在， 那么后者会覆盖前者
     *
     * 可在系统启动的时候加参数 -Dprofiles.active=xxx
     *
     * 优先级 系统启动参数 > 配置文件的属性
     *
     */
    String Default_Profiles_Active_Key = "profiles.active" ;


    /**
     * 配置文件的默认路径。 请确保在resources目录下. 即classpath中。
     *
     * 如果在resources某一文件夹下，请指明相对路径
     * 例：config/config1/application.properties
     *
     */
    String Default_Properties_Path =  "application.properties";


    /**
     * vertx-start相关组件在map中的key
     */
    String Key_Vertx_Start = "_vertx-start_";


    String Key_Container = "_container_";
    String Key_Vertx_Boot = "_boot_";




}
