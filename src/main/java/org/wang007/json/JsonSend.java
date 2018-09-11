package org.wang007.json;

import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.wang007.utils.CheckUtil;
import org.wang007.utils.CollectionUtils;

import java.time.Instant;
import java.util.*;

/**
 * json send zero-copy
 * <p>
 * <p>
 * vertx的使用者都知道，eventBus send json的话， 会copy一个json  但是这个copy很大可能是可以避免的。
 * <p>
 * 默认情况下， vertx jsonObject用的是LinkedHashMap。
 * 但是一般情况下，我们不需要有序。 所以{@link JsonSend} 默认用HashMap。 可通过构造方法的order控制。
 * <p>
 * JsonSend 一旦 send之后，JsonSend将会变成 immutable json，但是这个immutable也是尽可能的immutable json
 * <p>
 * 破坏send之后的immutable条件：先把json存入另一个json， 然后把这个另一个json存入JsonSend. 那么第一个json还是可变的。
 * <p>
 * 即是说，这个immutable是可以被破坏的。 但是你最好别这么做。
 * <p>
 * 还是那句话，  你要做傻逼， 没人能拦得住你。
 * <p>
 * 一旦存进JsonSend中的json，jsonArray，将变得不可变。
 * <p>
 * <p>
 * created by wang007 on 2018/9/1
 */
public class JsonSend extends JsonObject implements Sendable {

    private static final Logger logger = LoggerFactory.getLogger(JsonSend.class);

    public static final String Codec_Name = "jsonsend";

    private static final DeliveryOptions Default_Shared_Options = new ImmutableDeliveryOptions(Codec_Name);

    public static DeliveryOptions options() {
       return new DeliveryOptions().setCodecName(Codec_Name);
    }

    public static DeliveryOptions sharedOptions() {
        return Default_Shared_Options;
    }

    /**
     * 当前json是否被send， send之后，json将不可变
     */
    private boolean send = false;


    private static Map<String, Object> handleMap(JsonObject json) {
        json.getMap().forEach((k, v) -> {
            if (v instanceof Map) {
                throw new UnsupportedOperationException("unsupported map, but support json");
            } else if (v instanceof List) {
                throw new UnsupportedOperationException("unsupported list, but support jsonArray");
            }
            CheckUtil.checkAndCopy(v, false);

            if (v instanceof JsonObject) {
                CollectionUtils.wrapToImmutable((JsonObject) v);
            } else if (v instanceof JsonArray) {
                CollectionUtils.wrapToImmutable((JsonArray) v);
            }
        });
        Map<String, Object> map = json.getMap();
        CollectionUtils.wrapToImmutable(json);
        return map;
    }

    JsonSend(Map<String, Object> map) {
        super(map);
    }

    public JsonSend() {
        super(new HashMap<>());
    }

    /**
     * json中不能有 map, List.  原来json array中的json，json array 也会变得不可变
     * <p>
     * 经过构造方法之后， json将不可变.  属于过河拆桥
     *
     * @param json
     */
    public JsonSend(JsonObject json) {
        super(handleMap(json));
    }


    public JsonSend(boolean order) {
        super(order ? new LinkedHashMap<>() : new HashMap<>());
    }

    public JsonSend(String json) {
        super(json);
    }


    public JsonSend(Buffer buf) {
        super(buf);
    }

    private void assertSend() {
        if (isSend()) throw new IllegalStateException("Json was send,  so json is immutable obj");
    }

    @Override
    public JsonObject clear() {
        assertSend();
        return super.clear();
    }

    @Override
    public int readFromBuffer(int pos, Buffer buffer) {
        if (isSend()) {
            logger.warn("Json was send,  so json is immutable obj. not support read from buffer, so do nothing...");
            return pos;
        }
        return super.readFromBuffer(pos, buffer);
    }

    @Override
    public Map<String, Object> getMap() {
        return Collections.unmodifiableMap(super.getMap());
    }

    @Override
    public Iterator<Map.Entry<String, Object>> iterator() {
        return getMap().entrySet().iterator();
    }

    @Override
    public JsonSend put(String key, Enum value) {
        assertSend();
        super.put(key, value);
        return this;
    }

    @Override
    public JsonSend put(String key, CharSequence value) {
        assertSend();
        super.put(key, value);
        return this;
    }

    @Override
    public JsonSend put(String key, String value) {
        assertSend();
        super.put(key, value);
        return this;
    }

    @Override
    public JsonSend put(String key, Integer value) {
        assertSend();
        super.put(key, value);
        return this;

    }

    @Override
    public JsonSend put(String key, Long value) {
        assertSend();
        super.put(key, value);
        return this;
    }

    @Override
    public JsonSend put(String key, Double value) {
        assertSend();
        super.put(key, value);
        return this;
    }

    @Override
    public JsonSend put(String key, Float value) {
        assertSend();
        super.put(key, value);
        return this;
    }

    @Override
    public JsonSend put(String key, Boolean value) {
        assertSend();
        super.put(key, value);
        return this;
    }

    @Override
    public JsonSend putNull(String key) {
        assertSend();
        super.putNull(key);
        return this;
    }

    @Override
    public JsonSend put(String key, JsonObject value) {
        assertSend();
        CollectionUtils.wrapToImmutable(value);
        super.put(key, value);
        return this;
    }

    @Override
    public JsonSend put(String key, JsonArray value) {
        assertSend();
        CollectionUtils.wrapToImmutable(value);
        super.put(key, value);
        return this;
    }

    @Override
    public JsonSend put(String key, byte[] value) {
        assertSend();
        super.put(key, value);
        return this;
    }

    @Override
    public JsonSend put(String key, Instant value) {
        super.put(key, value);
        return this;
    }

    @Override
    public JsonSend put(String key, Object value) {
        assertSend();
        Objects.requireNonNull(key);
        if (value instanceof Map) {
            throw new UnsupportedOperationException("unsupported map, but support json");
        } else if (value instanceof List) {
            throw new UnsupportedOperationException("unsupported list, but support jsonArray");
        }
        if (value instanceof JsonObject) {
            put(key, (JsonObject) value);
        } else if (value instanceof JsonArray) {
            put(key, (JsonArray) value);
        }
        super.put(key, value);
        return this;
    }

    @Override
    public Object remove(String key) {
        assertSend();
        return super.remove(key);
    }

    @Override
    public JsonSend mergeIn(JsonObject other) {
        assertSend();
        super.mergeIn(other);
        return this;
    }

    @Override
    public JsonSend mergeIn(JsonObject other, boolean deep) {
        assertSend();
        super.mergeIn(other, deep);
        return this;
    }

    @Override
    public JsonSend mergeIn(JsonObject other, int depth) {
        assertSend();
        super.mergeIn(other, depth);
        return this;
    }

    @Override
    public Set<String> fieldNames() {
        return Collections.unmodifiableSet(super.fieldNames());
    }

    @Override
    public boolean isSend() {
        return send;
    }

    @Override
    public void send() {
        if (!send) this.send = true;
    }

    @Override
    public JsonObject toJson() {
        return this;
    }

    @Override
    public JsonSend copy() {
        JsonObject copy = super.copy();
        return new JsonSend(copy.getMap());
    }
}
