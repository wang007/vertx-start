package org.wang007.parse;

import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wang007.annotation.*;
import org.wang007.annotation.root.RootForComponent;
import org.wang007.constant.PropertyConst;
import org.wang007.exception.ErrorUsedAnnotationException;
import org.wang007.exception.RepetUsedAnnotationException;
import org.wang007.ioc.ComponentFactory;
import org.wang007.ioc.component.ComponentAndFieldsDescription;
import org.wang007.ioc.component.ComponentDescription;
import org.wang007.router.LoadRouter;
import org.wang007.utils.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
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


    private ComponentFactory factory = ComponentFactory.create() ;



    @Override
    public Map<String, String> parseProperties(String classpathPropertyFileName) {
        String fileName = StringUtils.trimToEmpty(classpathPropertyFileName);
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

        //解析profiles-active文件
        String activeName = result.get(PropertyConst.Default_Profiles_Active_Key);
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
            //找出被元注解Root的注解的class。
            //TODO 这里不知道有没有优化的方法
            //拿到这个类的所有注解，然后获取这个注解class，再查询这个注解class有没有被Root注解

            Annotation componentAnn = null;
            List<Annotation> otherAnns = new ArrayList<>();
            boolean isSingle = true;
            boolean isExist = false; //用于判断class中是否用了两个成为组件的注解

            Annotation[] anns = clz.getAnnotations();
            for(Annotation ann: anns) {
                if(ann instanceof Scope) {
                    isSingle = isSingle(clz, (Scope)ann);
                    //非注入注解， 也需要保存起来
                    otherAnns.add(ann);
                    continue;
                }
                //存在组件注解
                Class<? extends Annotation> annClz = ann.annotationType();
                RootForComponent rootAnno = annClz.getAnnotation(RootForComponent.class);
                if(rootAnno != null) {
                    if(isExist) throw new RepetUsedAnnotationException(clz + " 重复使用组件注解");
                    isExist = true;
                    componentAnn = ann;
                    continue;
                }
                //非组件型注解， 也需要保存起来
                otherAnns.add(ann);
            }
            //存在组件注解
            if(componentAnn != null) {
                ComponentDescription cd = factory.createComponentDescr(clz, componentAnn, isSingle, otherAnns);
                cds.add(cd);
            }
        });
        return cds;
    }

    @Override
    public List<? extends ComponentAndFieldsDescription> parseFieldsForCompoent(List<? extends ComponentDescription> cds) {

        List<? extends ComponentAndFieldsDescription> cfd = new ArrayList<>(cds.size());
        cds.forEach(cd -> cfd.add(factory.createComponentAndFieldsDescr(cd)));
        return cfd;
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

    /**
     * 是否为单例
     *
     * @param clz
     * @param scope
     * @return
     */
    protected boolean isSingle(Class<?> clz, Scope scope) {

        if(LoadRouter.class.isAssignableFrom(clz)) {
            logger.error("{} 不能使用@Scope注解", clz);
            throw new ErrorUsedAnnotationException(clz + " 不能使用@Scope注解, @Scope注解不能用于LoadRouter上, 实例策略内部已实现...");

        } else if(Verticle.class.isAssignableFrom(clz)) {
            logger.error("{} 不能使用@Scope注解", clz);
            throw new ErrorUsedAnnotationException(clz + " 不能使用@Scope注解, @Scope注解不能用于Verticle上, 实例策略内部已实现...");
        }
        Scope.Policy policy = scope.scopePolicy();
        if(policy == Scope.Policy.Prototype) return false;
        return true;
    }



    public static void main(String[] args) {

        System.out.println();

        long start = System.currentTimeMillis();

        ComponentParseImpl parse = new ComponentParseImpl();
        List<? extends ComponentDescription> list = parse.parseClassFromRoot(Vertx.vertx(), "org.wang007");
        List<? extends ComponentAndFieldsDescription> list1 = parse.parseFieldsForCompoent(list);
        System.out.println(list1);

        long end = System.currentTimeMillis();
        System.out.println("time -> " + (end - start));

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
                                if(isExist) logger.error("class重复存在, {}", loadClass);
                            }
                        }
                    }
                    continue;
                }
                //class文件
                if (filePath.endsWith(".class")) {
                    int index = filePath.lastIndexOf("/");
                    //去掉 .class 结尾
                    String fileName = filePath.substring(index == -1 ? 0: index + 1, filePath.length() - 6);
                    Class<?> loadClass = loadClass(fileName);
                    if (loadClass != null) {
                        boolean isExist = !classes.add(loadClass);
                        if(isExist) logger.error("class重复存在, {}", loadClass);
                    }
                }

            } else if ("jar".equals(fileType)) {
               JarFile jar = null ;
               try {
                 jar = ((JarURLConnection) dirOrFile.openConnection())
                           .getJarFile();
               } catch (IOException e) {
                   logger.warn("load classes failed... path -> {}", dotPath, e);
               }

               if(jar == null) continue;

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
                    if(fileName.endsWith("/")) continue;

                    if(fileName.charAt(0) == '/') {
                        fileName = fileName.substring(1);
                    }

                    //jar中文件或目录的路径，不与需要解析的路径匹配
                    if(!fileName.startsWith(slashPath)) continue ;

                    //class文件
                    if(fileName.endsWith(".class") && !jarEntry.isDirectory()) {
                        //去掉 .class 结尾
                        String filePath = fileName.substring(0, fileName.length() - 6);
                        Class<?> loadClass = loadClass(filePath.replace('/', '.'));
                        if (loadClass != null) {
                            boolean isExist = !classes.add(loadClass);
                            if(isExist) logger.error("class重复存在, {}", loadClass);
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
