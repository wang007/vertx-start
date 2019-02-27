package me.wang007.container;

import me.wang007.utils.StringUtils;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * 组件loader
 *
 * created by wang007 on 2019/2/26
 */
public class DefaultComponentLoader implements ComponentLoader {

    @Override
    public Component createComponent(Class<?> clz) {
        return createComponent(clz, new HashMap<>());
    }

    protected Component createComponent(Class<?> clz, Map<Class<?>, Component> map) {
        Component.Builder builder = Component.Builder.builder();
        builder.clazz(clz)
                .annotations(Arrays.asList(clz.getAnnotations()))
                .componentName(StringUtils.replaceFirstUpperCase(clz.getSimpleName()))
                .propertyFields(createPropertyField(clz));

        List<Class<?>> supers = findSupers(clz);
        if(supers.size() == 0) {
            builder.superComponents(Collections.emptySet());
        } else {
            Set<Component> superComponents = new HashSet<>();
            supers.forEach(superClz -> {
                Component superComponent = map.get(superClz);
                if(superComponent == null) {
                    superComponent = createComponent(superClz, map);
                    map.put(superClz, superComponent);
                }
                superComponents.add(superComponent);
            });
            builder.superComponents(superComponents);
        }
        return builder.build();
    }


    private List<PropertyField> createPropertyField(Class<?> clz) {
        Field[] fields = clz.getDeclaredFields();
        if(fields == null ||fields.length == 0) return Collections.emptyList();

        List<PropertyField> pfs = new ArrayList<>(fields.length);
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) continue; //过滤掉static属性
            PropertyField.Builder builder = PropertyField.Builder.builder();
            builder.field(field)
                    .fieldName(field.getName())
                    .fieldClass(field.getType());

            Annotation[] ans = field.getDeclaredAnnotations();
            List<Annotation> anList = Collections.emptyList();
            if(ans.length != 0) {
                anList = new ArrayList<>(ans.length);
                anList.addAll(Arrays.asList(ans));
            }
            builder.annotations(anList);
            pfs.add(builder.build());
        }
        return pfs;
    }


    @Override
    public Map<Class<?>, Component> loadComponents(Collection<Class<?>> classes,
                                                   List<Class<? extends Annotation>> load,
                                                   List<Class<?>> targetClz,
                                                   Set<Class<?>> targetFrom) {
        Map<Class<?>, Component> map = new HashMap<>();
        classes.forEach(clz -> {
            if (map.containsKey(clz)) return;
            Annotation[] as = clz.getAnnotations();
            for (Annotation a : as) {
                for (Class<? extends Annotation> ac : load) {
                    if(ac.isAssignableFrom(a.getClass())) {
                        map.put(clz, createComponent(clz, map));
                        return;
                    }
                }
            }

            for (Class target : targetClz) {
                if (clz.equals(target)) {
                    map.put(clz, createComponent(clz, map));
                    return;
                }
            }

            List<Class<?>> supers = findSupers(clz);
            for (Class<?> parent : supers) {
                if (targetFrom.contains(parent)) {
                    map.put(clz, createComponent(clz, map));
                    return;
                }
            }
        });
        return map;
    }


    /**
     * 查询该类的所有父类和接口。
     *
     * @param clz 目标类
     * @return
     */
    private List<Class<?>> findSupers(Class<?> clz) {
        Set<Class<?>> clzs = new HashSet<>();
        Class<?> superclass = clz.getSuperclass();
        addInterfaceClass(clzs, clz);   //添加接口 class
        do {
            if (superclass != null) addInterfaceClass(clzs, superclass);

            if (superclass != null && superclass != Object.class) {
                clzs.add(superclass);
                superclass = superclass.getSuperclass();
            }
        } while (superclass != null && superclass != Object.class);

        ArrayList<Class<?>> list = new ArrayList<>(clzs.size());
        list.addAll(clzs);
        return list;
    }

    private void addInterfaceClass(Collection<Class<?>> coll, Class<?> clz) {
        Class<?>[] interfaces = clz.getInterfaces();
        for (Class<?> ai : interfaces) {
            if (!(ai.isAssignableFrom(Serializable.class)
                    || ai.isAssignableFrom(Cloneable.class))) {
                coll.add(ai);
                addInterfaceClass(coll, ai);
            }
        }
    }

}
