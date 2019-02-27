package me.wang007.utils;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * created by wang007 on 2018/8/27
 */
public class CollectionUtils {

    private static Field JsonArray_Field;
    private static Field Json_Field;

    static {
        try {
            JsonArray_Field = JsonArray.class.getDeclaredField("list");
            JsonArray_Field.setAccessible(true);

            Json_Field = JsonObject.class.getDeclaredField("map");
            Json_Field.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new Error(e);
        }
    }


    /**
     * 判断集合是否为空，{@link java.util.Set}, {@link java.util.List} 可使用
     *
     * @param collect
     * @return
     */
    public static boolean isEmpty(Collection<?> collect) {
        return collect == null || collect.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> collect) {
        return !isEmpty(collect);
    }

    public static JsonArray wrapToImmutable(JsonArray array) {
        List list = array.getList();
        list = Collections.unmodifiableList(list);
        try {
            JsonArray_Field.set(array, list);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("set immutable json array failed...");
        }
        return array;
    }

    public static JsonObject wrapToImmutable(JsonObject json) {
        Map<String, Object> map = json.getMap();
        map = Collections.unmodifiableMap(map);
        try {
            Json_Field.set(json, map);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("set immutable json failed...");
        }
        return json;
    }



}
