package me.wang007.boot;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import me.wang007.container.LoadContainer;
import me.wang007.constant.PropertyConst;
import me.wang007.container.Component;
import me.wang007.container.PropertyField;
import me.wang007.exception.InjectException;
import me.wang007.exception.VertxStartException;
import me.wang007.utils.StringUtils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 配置文件的属性加载， 只能加载properties文件
 *
 * created by wang007 on 2019/2/27
 */
public class PropertiesLoader {

    private static final Logger logger = LoggerFactory.getLogger(PropertiesLoader.class);


    private final LoadContainer container;

    public PropertiesLoader(LoadContainer container) {
        this.container = container;
        if (container.started()) throw new IllegalStateException("Load container must be not started");
        container.registerLoadBy(me.wang007.annotation.Properties.class);
    }

    /**
     *
     * @param filePath
     * @return
     */
    public Map<String, String> loadProperties(String filePath) {
       return loadProperties(filePath, true);
    }


    /**
     * 加载properties文件
     *
     * @param filePath classpath下的properties文件路径
     * @param loadProfile 是否加载profile文件
     * @return
     */
    public Map<String, String> loadProperties(String filePath, boolean loadProfile) {

        String fileName = StringUtils.trimToEmpty(filePath);
        if (fileName.charAt(0) == '/') fileName = fileName.substring(1);
        if (StringUtils.isEmpty(fileName)) {
            logger.warn("项目配置文件为空, 解析配置文件的属性失败...");
            return Collections.emptyMap();
        }
        Properties prop = null;
        try (InputStream input = ClassLoader.getSystemResourceAsStream(fileName)) {
            prop = new Properties();
            prop.load(new InputStreamReader(input, StandardCharsets.UTF_8));
        } catch (Exception e) {
            logger.error("加载配置文件失败， 文件名: -> {}", fileName);
        }

        if (prop == null) return Collections.emptyMap();

        Map<String, String> result = new HashMap<>();
        prop.forEach((k, v) -> {
            Object oldVal = result.put((String) k, StringUtils.trimToEmpty((String) v));
            if (oldVal != null) logger.debug("key -> {} 已存在!", k);
        });

        if(!loadProfile) return Collections.unmodifiableMap(result);

        //解析profiles-active文件 先去系统文件下找，找不到再去结果集中找
        String activeName = System.getProperty(PropertyConst.Default_Profiles_Active_Key);
        if (StringUtils.isEmpty(activeName)) activeName = result.get(PropertyConst.Default_Profiles_Active_Key);

        if (StringUtils.isNotEmpty(activeName)) {
            int dotIndex = fileName.lastIndexOf(".");
            if (dotIndex == -1) {
                logger.warn("profiles-active文件名格式错误，加载profiles-active失败");
                return Collections.unmodifiableMap(result);
            }

            String prefix = fileName.substring(0, dotIndex);
            prefix = prefix + "-" + activeName;
            String activeFileName = prefix + fileName.substring(dotIndex);
            logger.info("profiles-active-file-name: -> {}", activeFileName);
            Properties activeProp = null;
            try (InputStream input = ClassLoader.getSystemResourceAsStream(activeFileName)) {
                activeProp = new Properties();
                activeProp.load(new InputStreamReader(input, "UTF-8"));
            } catch (Exception e) {
                logger.error("加载profiles-active配置文件失败， 文件名: -> {}", activeFileName);
            }

            if (activeProp != null)
                activeProp.forEach((k, v) -> result.put((String) k, StringUtils.trimToEmpty((String) v)));
        }
        return Collections.unmodifiableMap(result);

    }


    /**
     * 装载属性到指定的实体上
     *
     * @param propertiesClz 指定类，该类必须有{@link me.wang007.annotation.Properties} 注解
     * @param <E>
     * @return propertiesClz类的实例
     */
    public <E> E loadFor(Class<E> propertiesClz, Map<String, String> result) {
        Objects.requireNonNull(propertiesClz, "require");
        Component component = container.getComponent(propertiesClz);
        if(component == null) {
            throw new NullPointerException("not found component, require " + propertiesClz.getName() + " exist @Properties");
        }
        Object instance;
        try {
            instance = component.clazz.newInstance();
        } catch (Exception e) {
            throw new VertxStartException(e);
        }

        me.wang007.annotation.Properties pr = component.getAnnotation(me.wang007.annotation.Properties.class);
        String prefix = StringUtils.trimToEmpty(pr.prefix());
        if(StringUtils.isEmpty(prefix)) prefix = StringUtils.trimToEmpty(pr.value());
        String pre = prefix; //fuck, shit for lambda

        component.getAllPropertis().forEach(pf -> {
            String key = pre + "." + pf.fieldName;
            injectValue0(component, pf, instance, result.get(key));
        });
        return (E) instance;
    }


    /**
     * 注入属性
     * @param component     组件
     * @param propertyField 属性域
     * @param instance      组件所对应的实例
     * @param value         属性
     */
    private void injectValue0(Component component,
                              PropertyField propertyField, Object instance, String value) {
        if (value == null) {
            logger.warn("class: {}, field: {}  set value failed, not found value...",
                    component.clazz.getName(), propertyField.fieldName);
            return;
        }
        try {
            Class<?> fieldClass = propertyField.fieldClass;
            propertyField.field.setAccessible(true);
            if (fieldClass == String.class) {
                propertyField.field.set(instance, value);
            } else if (fieldClass.isPrimitive()) { //基本类型

                if (fieldClass == Integer.TYPE) {
                    propertyField.field.setInt(instance, Integer.valueOf(value));

                } else if (fieldClass == Short.TYPE) {
                    propertyField.field.setShort(instance, Short.valueOf(value));

                } else if (fieldClass == Float.TYPE) {
                    propertyField.field.setFloat(instance, Float.valueOf(value));

                } else if (fieldClass == Boolean.TYPE) {
                    propertyField.field.setBoolean(instance, Boolean.valueOf(value));

                } else if (fieldClass == Long.TYPE) {
                    propertyField.field.setLong(instance, Long.valueOf(value));

                } else if (fieldClass == Double.TYPE) {
                    propertyField.field.setDouble(instance, Double.valueOf(value));

                } else if (fieldClass == Character.TYPE) {
                    propertyField.field.setChar(instance, value.length() > 0 ? value.charAt(0) : ' ');

                } else if (fieldClass == Byte.TYPE) {
                    propertyField.field.setByte(instance, Byte.valueOf(value));
                }

            } else if (fieldClass == Integer.class) {
                propertyField.field.set(instance, Integer.valueOf(value));

            } else if (fieldClass == Short.class) {
                propertyField.field.set(instance, Short.valueOf(value));

            } else if (fieldClass == Boolean.class) {
                propertyField.field.set(instance, Boolean.valueOf(value));

            } else if (fieldClass == Long.class) {
                propertyField.field.set(instance, Long.valueOf(value));

            } else if (fieldClass == Float.class) {
                propertyField.field.set(instance, Float.valueOf(value));
            } else {    //其他类型不处理
                logger.warn("not known type.  class: " + component.clazz.getName() + ",  field: " + propertyField.fieldName + ", field-type:" + propertyField.fieldClass);
            }
        } catch (IllegalAccessException e) {
            throw new InjectException("illegal access, inject value failed.  class: " + component.clazz.getName() + ",  field: " + propertyField.fieldName + ", field-type:" + propertyField.fieldClass);
        }
    }



}
