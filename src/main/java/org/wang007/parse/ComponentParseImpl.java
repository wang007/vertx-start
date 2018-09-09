package org.wang007.parse;

import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wang007.constant.PropertyConst;
import org.wang007.ioc.ComponentFactory;
import org.wang007.ioc.component.ComponentAndFieldsDescription;
import org.wang007.ioc.component.ComponentDescription;
import org.wang007.utils.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * created by wang007 on 2018/8/23
 */
public class ComponentParseImpl implements ComponentParse {

    private static final Logger logger = LoggerFactory.getLogger(ComponentParseImpl.class);


    private ComponentFactory factory = ComponentFactory.create();


    @Override
    public Map<String, String> parseProperties(String classpathPropertyFilePath) {
        String fileName = StringUtils.trimToEmpty(classpathPropertyFilePath);
        if (fileName.charAt(0) == '/') fileName = fileName.substring(1);
        if ("".equals(fileName)) {
            logger.warn("项目配置文件为空, 解析配置文件的属性失败...");
            return Collections.emptyMap();
        }
        Properties prop = null;
        try (InputStream input = ClassLoader.getSystemResourceAsStream(fileName)) {
            prop = new Properties();
            prop.load(new InputStreamReader(input, "UTF-8"));
        } catch (Exception e) {
            logger.error("加载配置文件失败， 文件名: -> {}", fileName);
        }

        if (prop == null) return Collections.emptyMap();

        Map<String, String> result = new HashMap<>();
        prop.forEach((k, v) -> {
            Object oldVal = result.put((String) k, StringUtils.trimToEmpty((String) v));
            if (oldVal != null) logger.debug("key -> {} 已存在!", k);
        });

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
            logger.debug("profiles-active-file-name: -> {}", activeFileName);
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


    @Override
    public List<? extends ComponentDescription> parseClassFromRoot(Vertx vertx, String... basePaths) {
        Set<String> paths = new HashSet<>(Arrays.asList(basePaths));
        //TODO 一个basePath的路径， startWith 另一个basePath的路径， 那么这另一个路径是应该被剔除的

        Set<Class<?>> classes = new LinkedHashSet<>();
        //根据basePaths解析出class
        for (String path : paths) getClassesByPath(classes, path);

        List<ComponentDescription> cds = new ArrayList<>(classes.size());
        classes.forEach(clz -> {
            ComponentDescription cd = factory.createComponentDescr(clz);
            if (cd != null) cds.add(cd);
        });
        return cds;
    }

    @Override
    public List<? extends ComponentAndFieldsDescription> parseFieldsForComponents(List<? extends ComponentDescription> cds) {
        List<? extends ComponentAndFieldsDescription> cfd = new ArrayList<>(cds.size());
        cds.forEach(cd -> cfd.add(factory.createComponentAndFieldsDescr(cd)));
        return cfd;
    }

    @Override
    public <T extends ComponentAndFieldsDescription> T createComponent(Class<?> clz) {
        ComponentDescription descr = factory.createComponentDescr(clz);
        return descr == null ? null : factory.createComponentAndFieldsDescr(descr);
    }

    @Override
    public <T extends ComponentAndFieldsDescription> Object newInstance(T cd) {
        return factory.newInstance(cd);
    }

    @Override
    public ComponentParse setComponentFactory(ComponentFactory factory) {
        Objects.requireNonNull(factory, "factory is required");
        this.factory = factory;
        return this;
    }


    public static void main(String[] args) {


        System.out.println(args);
        System.out.println();

        Properties properties = System.getProperties();
        System.out.println(properties);

        long start = System.currentTimeMillis();

        ComponentParseImpl parse = new ComponentParseImpl();
        List<? extends ComponentDescription> list = parse.parseClassFromRoot(Vertx.vertx(), "org.wang007");
        List<? extends ComponentAndFieldsDescription> list1 = parse.parseFieldsForComponents(list);
        System.out.println(list1);

        long end = System.currentTimeMillis();
        System.out.println("time -> " + (end - start));

        list1.forEach(cd -> {

            cd.propertyDescriptions.forEach(ipd -> {
                System.out.println(ipd.fieldClass);


                //System.out.println(ipd.fieldName + ": " + ipd.fieldClass);
            });
            System.out.println();
        });

        System.out.println();
        ComponentAndFieldsDescription cd = list1.get(6);
        cd.propertyDescriptions.forEach(ipd -> {

            if (ipd.fieldClass == Integer.TYPE) {
                System.out.println("hh: " + ipd.fieldName + "  tt: " + ipd.fieldClass.isPrimitive());
            }

            if (ipd.fieldClass == Integer.class) {
                System.out.println(ipd.fieldName + " -> " + ipd.fieldClass.isPrimitive());
            }


            if (ipd.fieldClass == String.class) {
                System.out.println(ipd.fieldName);
            }

        });


    }


    /**
     * @param classes
     * @param dotPath 以 "." 分割包路径的path
     */
    private void getClassesByPath(Set<Class<?>> classes, String dotPath) {

        Enumeration<URL> dirOrFiles = null;
        // 以 “/”分割的path
        String slashPath = dotPath.replace(".", "/");
        try {
            dirOrFiles = Thread.currentThread().getContextClassLoader().getResources(slashPath);
        } catch (IOException e) {
            logger.error("load class failed, path = {}", dotPath, e);
        }
        if (dirOrFiles == null) return; //加载文件或目录的路径出错

        while (dirOrFiles.hasMoreElements()) {
            URL dirOrFile = dirOrFiles.nextElement();
            //文件类型， file or jar
            String fileType = dirOrFile.getProtocol();

            if ("file".equals(fileType)) {
                String filePath = dirOrFile.getFile();
                File file = new File(filePath);

                if (!file.exists()) {
                    logger.warn("path: {}, file not exist", filePath);
                    continue;
                }
                //目录
                if (file.isDirectory()) {
                    File[] files = file.listFiles(f -> f.isDirectory() || f.getName().endsWith(".class"));
                    if (files == null) continue;

                    for (File f : files) {
                        String fileName = f.getName();
                        if (f.isDirectory()) getClassesByPath(classes, dotPath + '.' + fileName);
                        else if (f.getName().endsWith(".class")) {
                            //去掉 .class 结尾
                            fileName = fileName.substring(0, fileName.length() - 6);
                            Class<?> loadClass = loadClass(dotPath + '.' + fileName);
                            if (loadClass != null) {
                                boolean isExist = !classes.add(loadClass);
                                if (isExist) logger.error("class重复存在, {}", loadClass);
                            }
                        }
                    }
                    continue;
                }
                //class文件
                if (filePath.endsWith(".class")) {
                    int index = filePath.lastIndexOf("/");
                    //去掉 .class 结尾
                    String fileName = filePath.substring(index == -1 ? 0 : index + 1, filePath.length() - 6);
                    Class<?> loadClass = loadClass(fileName);
                    if (loadClass != null) {
                        boolean isExist = !classes.add(loadClass);
                        if (isExist) logger.error("class重复存在, {}", loadClass);
                    }
                }

            } else if ("jar".equals(fileType)) {
                JarFile jar = null;
                try {
                    jar = ((JarURLConnection) dirOrFile.openConnection())
                            .getJarFile();
                } catch (IOException e) {
                    logger.warn("load classes failed... path -> {}", dotPath, e);
                }

                if (jar == null) continue;

                Enumeration<JarEntry> itemsForJar = jar.entries();
                while (itemsForJar.hasMoreElements()) {
                    JarEntry jarEntry = itemsForJar.nextElement();

                    /**
                     * 一个jar可能包括META-INF等其他非class文件。
                     *
                     * 这里扫描的目录和文件都会展开
                     * 即就是已经进行了递归到内层了
                     * 而且如果是目录， 以 “/” 结尾 忽略
                     * 如果是以 “.class” 结尾， 解析生成class
                     *
                     */
                    String fileName = jarEntry.getName();

                    //目录
                    if (fileName.endsWith("/")) continue;

                    if (fileName.charAt(0) == '/') {
                        fileName = fileName.substring(1);
                    }

                    //jar中文件或目录的路径，不与需要解析的路径匹配
                    if (!fileName.startsWith(slashPath)) continue;

                    //class文件
                    if (fileName.endsWith(".class") && !jarEntry.isDirectory()) {
                        //去掉 .class 结尾
                        String filePath = fileName.substring(0, fileName.length() - 6);
                        Class<?> loadClass = loadClass(filePath.replace('/', '.'));
                        if (loadClass != null) {
                            boolean isExist = !classes.add(loadClass);
                            if (isExist) logger.error("class重复存在, {}", loadClass);
                        }
                    }
                }

            } // fileType = jar

        } //foreach dirOrFiles
    }

    /**
     * 根据类路径， 加载 class
     *
     * @param dotPath 类路径
     * @return
     */
    private Class<?> loadClass(String dotPath) {
        try {
            return Thread.currentThread().getContextClassLoader()
                    .loadClass(dotPath);
        } catch (ClassNotFoundException e) {
            logger.warn("load class failed, className -> {}", dotPath, e);
        }
        return null;
    }

}
