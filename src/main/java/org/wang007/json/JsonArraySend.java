package org.wang007.json;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.wang007.utils.CheckUtil;
import org.wang007.utils.CollectionUtils;

import java.time.Instant;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * json-array send zero-copy
 *
 * JsonArraySend 一旦 send之后，JsonSend将会变成 immutable json，但是这个immutable也是尽可能的immutable json
 *
 *破坏send之后的immutable条件：先把json存入另一个json， 然后把这个另一个json存入JsonArraySend. 那么第一个json还是可变的。
 *
 *即是说，这个immutable是可以被破坏的。 但是你最好别这么做。
 *
 * 还是那句话，  你要做傻逼， 没人能拦得住你。
 *
 * 一旦存进JsonArraySend中的json，jsonArray，将变得不可变。
 *
 * created by wang007 on 2018/9/2
 */
public class JsonArraySend extends JsonArray implements Sendable {


    private static final Logger logger = LoggerFactory.getLogger(JsonArraySend.class);
    /**
     * 当前json array 是否被send， send之后，json array将不可变
     */
    private  boolean send;

    private static List handleList(JsonArray array) {
        array.forEach(v -> {
            if(v instanceof Map) {
                throw new UnsupportedOperationException("unsupported map, but support json");
            } else if(v instanceof List) {
                throw new UnsupportedOperationException("unsupported list, but support jsonArray");
            }
            CheckUtil.checkAndCopy(v, false);
            if(v instanceof JsonObject) {
                CollectionUtils.wrapToImmutable((JsonObject) v);
            } else if(v instanceof JsonArray) {
                CollectionUtils.wrapToImmutable((JsonArray) v);
            }
        });
        List list = array.getList();
        CollectionUtils.wrapToImmutable(array);
        return list;

    }

    public JsonArraySend(String json) {
        super(json);
    }

    public JsonArraySend() {
        super();
    }

    /**
     * json array中不能有 map, List.  原来json array中的json，json array 也会变得不可变
     *
     * 经过构造方法之后， json array将不可变.  属于过河拆桥
     * @param array
     */
    public JsonArraySend(JsonArray array) {
        super(handleList(array));
    }

    public JsonArraySend(Buffer buf) {
        super(buf);
    }

    private void assertSend() {
        if(isSend()) throw new IllegalStateException("Json was send,  so json is immutable obj. ");
    }

    @Override
    public JsonArray clear() {
        assertSend();
        return super.clear();
    }

    @Override
    public List getList() {
        return Collections.unmodifiableList(super.getList());
    }

    @Override
    public Iterator<Object> iterator() {
        return getList().iterator();
    }

    @Override
    public int readFromBuffer(int pos, Buffer buffer) {
        if(isSend()) {
            logger.warn("Json was send,  so json is immutable obj. not support read from buffer, so do nothing...");
            return pos;
        }
        return super.readFromBuffer(pos, buffer);
    }

    @Override
    public JsonArray add(Enum value) {
        assertSend();
        return super.add(value);
    }

    @Override
    public JsonArray add(CharSequence value) {
        assertSend();
        return super.add(value);
    }

    @Override
    public JsonArray add(String value) {
        assertSend();
        return super.add(value);
    }

    @Override
    public JsonArray add(Integer value) {
        assertSend();
        return super.add(value);
    }

    @Override
    public JsonArray add(Long value) {
        assertSend();
        return super.add(value);
    }

    @Override
    public JsonArray add(Double value) {
        assertSend();
        return super.add(value);
    }

    @Override
    public JsonArray add(Float value) {
        assertSend();
        return super.add(value);
    }

    @Override
    public JsonArray add(Boolean value) {
        assertSend();
        return super.add(value);
    }

    @Override
    public JsonArray addNull() {
        assertSend();
        return super.addNull();
    }

    @Override
    public JsonArray add(JsonObject value) {
        assertSend();
        CollectionUtils.wrapToImmutable(value);
        return super.add(value);
    }

    @Override
    public JsonArray add(JsonArray value) {
        assertSend();
        CollectionUtils.wrapToImmutable(value);
        return super.add(value);
    }

    @Override
    public JsonArray add(byte[] value) {
        assertSend();
        return super.add(value);
    }

    @Override
    public JsonArray add(Instant value) {
        assertSend();
        return super.add(value);
    }

    @Override
    public JsonArray add(Object value) {
        assertSend();
        if(value instanceof Map) {
            throw new UnsupportedOperationException("unsupported map, but support json");
        } else if(value instanceof List) {
            throw new UnsupportedOperationException("unsupported list, but support jsonArray");
        }
        if(value instanceof JsonObject) {
            add((JsonObject) value);
        } else if(value instanceof JsonArray) {
            add((JsonArray)value);
        }
        return super.add(value);
    }

    @Override
    public JsonArray addAll(JsonArray array) {
        assertSend();
        array.forEach(this::add);
        return this;
    }

    @Override
    public boolean remove(Object value) {
        assertSend();
        return super.remove(value);
    }

    @Override
    public Object remove(int pos) {
        assertSend();
        return super.remove(pos);
    }

    @Override
    public boolean isSend() {
        return send;
    }

    @Override
    public void send() {
        if(!send) send = true;
    }

    @Override
    public JsonObject toJson() {
        return new JsonObject().put(JsonArray_Key, this);
    }
}
