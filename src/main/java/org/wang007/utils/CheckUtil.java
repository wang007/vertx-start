package org.wang007.utils;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.time.format.DateTimeFormatter.ISO_INSTANT;

/**
 * created by wang007 on 2018/9/1
 */
public class CheckUtil {


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


    // copy from vert.x
    @SuppressWarnings("unchecked")
   public static Object checkAndCopy(Object val, boolean copy) {
        if (val == null) {
            // OK
        } else if (val instanceof Number && !(val instanceof BigDecimal)) {
            // OK
        } else if (val instanceof Boolean) {
            // OK
        } else if (val instanceof String) {
            // OK
        } else if (val instanceof Character) {
            // OK
        } else if (val instanceof CharSequence) {
            val = val.toString();
        } else if (val instanceof JsonObject) {
            if (copy) {
                val = ((JsonObject) val).copy();
            }
        } else if (val instanceof JsonArray) {
            if (copy) {
                val = ((JsonArray) val).copy();
            }
        } else if (val instanceof Map) {
            if (copy) {
                val = (new JsonObject((Map)val)).copy();
            } else {
                val = new JsonObject((Map)val);
            }
        } else if (val instanceof List) {
            if (copy) {
                val = (new JsonArray((List)val)).copy();
            } else {
                val = new JsonArray((List)val);
            }
        } else if (val instanceof byte[]) {
            val = Base64.getEncoder().encodeToString((byte[])val);
        } else if (val instanceof Instant) {
            val = ISO_INSTANT.format((Instant) val);
        } else {
            throw new IllegalStateException("Illegal type in JsonObject: " + val.getClass());
        }
        return val;
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
