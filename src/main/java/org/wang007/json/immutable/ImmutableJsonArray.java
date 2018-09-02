package org.wang007.json.immutable;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wang007.json.Sendable;
import org.wang007.json.immutable.iter.ImmutableJsonArrayIter;

import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

/**
 * 尽可能的immutable json array 只要你是正常使用，它都是immutable json array
 *
 * 你要做傻逼， 没人能拦的住你
 *
 * 最傻逼一个行为： 就是先预先存进json，这个json作为value，外部持有引用，通过这个引用来改变的这个json（value）来破坏immutable
 *
 * created by wang007 on 2018/9/1
 */
public class ImmutableJsonArray extends JsonArray implements Sendable {

    private static final Logger logger = LoggerFactory.getLogger(ImmutableJsonArray.class);

    public ImmutableJsonArray(JsonArray jsonArray) {
        super(jsonArray.getList());
    }

    public ImmutableJsonArray(String json) {
        super(json);
    }

    public ImmutableJsonArray() {
    }

    public ImmutableJsonArray(List list) {
        super(list);
    }

    public ImmutableJsonArray(Buffer buf) {
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
    public int readFromBuffer(int pos, Buffer buffer) {
        logger.warn("immutable json array not support read from buffer, so do nothing...");
        return pos;
    }

    @Override
    public Iterator<Object> iterator() {
        return new ImmutableJsonArrayIter(super.getList().iterator());
    }

    @Override
    public JsonObject getJsonObject(int pos) {
        JsonObject jsonObject = super.getJsonObject(pos);
        return new ImmutableJson(jsonObject);
    }

    @Override
    public JsonArray getJsonArray(int pos) {
        JsonArray jsonArray = super.getJsonArray(pos);
        return new ImmutableJsonArray(jsonArray);
    }



    @Override
    public JsonArray add(Enum value) {
        throw new UnsupportedOperationException("immutable jsonArray");
    }

    @Override
    public JsonArray add(CharSequence value) {
        throw new UnsupportedOperationException("immutable jsonArray");
    }

    @Override
    public JsonArray add(String value) {
        throw new UnsupportedOperationException("immutable jsonArray");
    }

    @Override
    public JsonArray add(Integer value) {
        throw new UnsupportedOperationException("immutable jsonArray");
    }

    @Override
    public JsonArray add(Long value) {
        throw new UnsupportedOperationException("immutable jsonArray");
    }

    @Override
    public JsonArray add(Double value) {
        throw new UnsupportedOperationException("immutable jsonArray");
    }

    @Override
    public JsonArray add(Float value) {
        throw new UnsupportedOperationException("immutable jsonArray");
    }

    @Override
    public JsonArray add(Boolean value) {
        throw new UnsupportedOperationException("immutable jsonArray");
    }

    @Override
    public JsonArray addNull() {
        throw new UnsupportedOperationException("immutable jsonArray");
    }

    @Override
    public JsonArray add(JsonObject value) {
        throw new UnsupportedOperationException("immutable jsonArray");
    }

    @Override
    public JsonArray add(JsonArray value) {
        throw new UnsupportedOperationException("immutable jsonArray");
    }

    @Override
    public JsonArray add(byte[] value) {
        throw new UnsupportedOperationException("immutable jsonArray");
    }

    @Override
    public JsonArray add(Instant value) {
        throw new UnsupportedOperationException("immutable jsonArray");
    }

    @Override
    public JsonArray add(Object value) {
        throw new UnsupportedOperationException("immutable jsonArray");
    }

    @Override
    public JsonArray addAll(JsonArray array) {
        throw new UnsupportedOperationException("immutable jsonArray");
    }

    @Override
    public boolean remove(Object value) {
        throw new UnsupportedOperationException("immutable jsonArray");
    }

    @Override
    public Object remove(int pos) {
        throw new UnsupportedOperationException("immutable jsonArray");
    }

    @Override
    public List getList() {
        throw new UnsupportedOperationException("immutable jsonArray");
    }

    @Override
    public Stream<Object> stream() {
        throw new UnsupportedOperationException("immutable jsonArray");
    }

    @Override
    public JsonArray clear() {
        throw new UnsupportedOperationException("immutable jsonArray");
    }

    @Override
    public JsonObject toJson() {
        return new JsonObject().put(JsonArray_Key, this);
    }
}
