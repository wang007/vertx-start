package org.wang007.json;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.wang007.json.iter.ImmutableJsonIter;
import org.wang007.utils.CheckUtil;

import java.time.Instant;
import java.util.*;


/**
 *
 * created by wang007 on 2018/9/1
 */
public class ImmutableJson extends JsonObject implements Sendable {

    public ImmutableJson(JsonObject json) {
        super(json.getMap());
    }

    public ImmutableJson(String jsonStr) {
        super(jsonStr);
    }

    public ImmutableJson() {
        super();
    }

    public ImmutableJson(Map<String, Object> map) {
        super(map);
    }

    public ImmutableJson(Buffer buf) {
        super(buf);
    }


    @Override
    public boolean isSend() {
        return true;
    }

    @Override
    public void send() {
        //NOOP
    }


    @Override
    public JsonObject getJsonObject(String key) {
        Objects.requireNonNull(key);
        Object val = super.getMap().get(key);
        if (val instanceof Map) {
            return new ImmutableJson((Map)val);

        } else if(val instanceof JsonObject) {
            return new ImmutableJson((JsonObject)val);
        }
        throw new IllegalStateException("Illegal type in immutable json: " + val.getClass());
    }

    @Override
    public JsonObject getJsonObject(String key, JsonObject def) {
        return super.getJsonObject(key, def);
    }

    @Override
    public JsonArray getJsonArray(String key) {
        Objects.requireNonNull(key);
        Object val = super.getMap().get(key);
        if (val instanceof List) {
            return new ImmutableJsonArray((List)val);

        } else if (val instanceof JsonArray) {
            return new ImmutableJsonArray((JsonArray)val);
        }
        throw new IllegalStateException("Illegal type in immutable json: " + val.getClass());

    }

    @Override
    public JsonArray getJsonArray(String key, JsonArray def) {
        return super.getJsonArray(key, def);
    }

    @Override
    public Object getValue(String key) {
        Object val = super.getValue(key);
        val= CheckUtil.checkAndCopy(val, false);
        if(val instanceof JsonArray) {
            return getJsonArray(key);

        } else if(val instanceof JsonObject) {
            return getJsonObject(key);
        }
        throw new IllegalStateException("Illegal type in immutable json: " + val.getClass());
    }

    @Override
    public Object getValue(String key, Object def) {
        return super.getValue(key, def);
    }

    @Override
    public <T> T mapTo(Class<T> type) {
        throw new UnsupportedOperationException("immutable json");
    }

    @Override
    public JsonObject put(String key, Enum value) {
        throw new UnsupportedOperationException("immutable json");
    }

    @Override
    public JsonObject put(String key, CharSequence value) {
        throw new UnsupportedOperationException("immutable json");
    }

    @Override
    public JsonObject put(String key, String value) {
        throw new UnsupportedOperationException("immutable json");
    }

    @Override
    public JsonObject put(String key, Integer value) {
        throw new UnsupportedOperationException("immutable json");
    }

    @Override
    public JsonObject put(String key, Long value) {
        throw new UnsupportedOperationException("immutable json");
    }

    @Override
    public JsonObject put(String key, Double value) {
        throw new UnsupportedOperationException("immutable json");
    }

    @Override
    public JsonObject put(String key, Float value) {
        throw new UnsupportedOperationException("immutable json");
    }

    @Override
    public JsonObject put(String key, Boolean value) {
        throw new UnsupportedOperationException("immutable json");
    }

    @Override
    public JsonObject putNull(String key) {
        throw new UnsupportedOperationException("immutable json");
    }

    @Override
    public JsonObject put(String key, JsonObject value) {
        throw new UnsupportedOperationException("immutable json");
    }

    @Override
    public JsonObject put(String key, JsonArray value) {
        throw new UnsupportedOperationException("immutable json");
    }

    @Override
    public JsonObject put(String key, byte[] value) {
        throw new UnsupportedOperationException("immutable json");
    }

    @Override
    public JsonObject put(String key, Instant value) {
        throw new UnsupportedOperationException("immutable json");
    }

    @Override
    public JsonObject put(String key, Object value) {
        throw new UnsupportedOperationException("immutable json");
    }

    @Override
    public Object remove(String key) {
        throw new UnsupportedOperationException("immutable json");
    }

    @Override
    public JsonObject mergeIn(JsonObject other) {
        throw new UnsupportedOperationException("immutable json");
    }

    @Override
    public JsonObject mergeIn(JsonObject other, boolean deep) {
        throw new UnsupportedOperationException("immutable json");
    }

    @Override
    public JsonObject mergeIn(JsonObject other, int depth) {
        throw new UnsupportedOperationException("immutable json");
    }
    @Override
    public Map<String, Object> getMap() {
        return Collections.unmodifiableMap(super.getMap());
    }

    @Override
    public Iterator<Map.Entry<String, Object>> iterator() {
        return new ImmutableJsonIter(super.getMap().entrySet().iterator());
    }

    @Override
    public int readFromBuffer(int pos, Buffer buffer) {
        throw new UnsupportedOperationException("immutable json");
    }

    @Override
    public int size() {
        return super.size();
    }

    @Override
    public JsonObject clear() {
        throw new UnsupportedOperationException("immutable json");
    }

    @Override
    public boolean isEmpty() {
        return super.isEmpty();
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public Set<String> fieldNames() {
        return Collections.unmodifiableSet(super.fieldNames());
    }

}
