package org.wang007.json.immutable;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wang007.json.Sendable;
import org.wang007.json.immutable.iter.ImmutableJsonIter;
import org.wang007.utils.CheckUtil;

import java.time.Instant;
import java.util.*;
import java.util.stream.Stream;


/**
 * 不可变的json， 也是尽可能的做到 immutable。 只要你是正常使用，它都是immutable的。
 *
 * 你要做傻逼， 没人能拦的住你
 *
 * 最傻逼一个行为： 就是先预先存进json，这个json作为value，外部持有引用，通过这个引用来改变的这个json（value）来破坏immutable
 *
 * created by wang007 on 2018/9/1
 */
public class ImmutableJson extends JsonObject implements Sendable {

    private static final Logger logger = LoggerFactory.getLogger(ImmutableJson.class);


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
        CheckUtil.checkAndCopy(val, false);
        if(val instanceof JsonArray) {
            return getJsonArray(key);

        } else if(val instanceof JsonObject) {
            return getJsonObject(key);
        }
        return val;
        //throw new IllegalStateException("Illegal type in immutable json: " + val.getClass());
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
        throw new UnsupportedOperationException("immutable json");
    }

    @Override
    public Stream<Map.Entry<String, Object>> stream() {
        throw new UnsupportedOperationException("immutable json");
    }

    @Override
    public Iterator<Map.Entry<String, Object>> iterator() {
        return new ImmutableJsonIter(super.getMap().entrySet().iterator());
    }

    @Override
    public int readFromBuffer(int pos, Buffer buffer) {
        logger.warn("immutable json not support read from buffer, so do nothing...");
        return pos;
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

    @Override
    public JsonObject toJson() {
        return this;
    }
}
