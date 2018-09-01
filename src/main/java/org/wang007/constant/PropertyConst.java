package org.wang007.constant;

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
     */
    String Default_Profiles_Active_Key = "profiles.active" ;

}
