package me.wang007.container;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * created by wang007 on 2019/2/26
 */
public abstract class AbstractLoadContainer<E extends AbstractLoadContainer> implements LoadContainer<E> {

    protected static final Logger logger = LoggerFactory.getLogger(AbstractLoadContainer.class);

    /**
     * 目标类被注解的集合
     */
    private final List<Class<? extends Annotation>> loadByAnnotation = new ArrayList<>();

    /**
     * 目标类
     */
    private final List<Class<?>> loadByTargetClz = new ArrayList<>();

    /**
     * 目标类的子类
     */
    private final List<Class<?>> loadByFrom = new ArrayList<>();


    protected ComponentLoader componentLoader = new DefaultComponentLoader();


    private final Map<Class<?>, Component> componentMap = new HashMap<>();


    protected final List<Class<? extends Annotation>> getLoadByAnnotation() {
        return Collections.unmodifiableList(loadByAnnotation);
    }

    protected final List<Class> getLoadByTargetClz() {
        return Collections.unmodifiableList(loadByTargetClz);
    }

    protected final List<Class> getLoadByFrom() {
        return Collections.unmodifiableList(loadByFrom);
    }

    protected final Map<Class<?>, Component> componentMap() {
        return Collections.unmodifiableMap(componentMap);
    }


    @SuppressWarnings("unchecked")
    private E self() {
        return (E)this;
    }

    @Override
    public E registerLoadBy(Class<? extends Annotation> loadBy) {
        Objects.requireNonNull(loadBy, "register require not null");
        synchronized (loadByAnnotation) {
            addIfAbsent(loadByAnnotation, loadBy);
        }
        return self();
    }

    @Override
    public E register(Class<?> targetClz) {
        Objects.requireNonNull(targetClz, "register  require not null");
        synchronized (loadByTargetClz) {
            addIfAbsent(loadByTargetClz, targetClz);
        }
        return self();
    }

    @Override
    public E registerFrom(Class<?> fromClz) {
        Objects.requireNonNull(fromClz, "register  require not null");
        synchronized (loadByFrom) {
            addIfAbsent(loadByFrom, fromClz);
        }
        return self();
    }


    private <E> void addIfAbsent(List<E> list, E obj) {
        if(!list.contains(obj)) list.add(obj);
    }

    protected synchronized void loadComponents(String ...basePaths) {

        Set<String> paths = new HashSet<>(Arrays.asList(basePaths));
        //TODO 一个basePath的路径， startWith 另一个basePath的路径， 那么这另一个路径是应该被剔除的

        if(basePaths.length == 0) {
            logger.warn("not found base path...");
        }

        Set<Class<?>> classes = new LinkedHashSet<>();
        //根据basePaths解析出class
        for (String path : paths) getClassesByPath(classes, path);

        Set<Class<?>> loadFromSet = new HashSet<>(loadByFrom.size());
        loadFromSet.addAll(loadByFrom);
        Map<Class<?>, Component> map =
                componentLoader.loadComponents(classes, loadByAnnotation, loadByTargetClz, loadFromSet);
        map.forEach(componentMap::put);
    }


    /**
     * 加载class
     *
     * @param classes 装载class的容器
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
            return Default_ClassLoader.loadClass(dotPath);
        } catch (ClassNotFoundException e) {
            logger.warn("load class failed, className -> {}", dotPath, e);
        }
        return null;
    }


}
