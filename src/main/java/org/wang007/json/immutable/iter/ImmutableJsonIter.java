package org.wang007.json.immutable.iter;


import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.wang007.json.immutable.ImmutableJson;
import org.wang007.json.immutable.ImmutableJsonArray;
import org.wang007.utils.CheckUtil;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * copy from vert.x JsonObject
 *
 * created by wang007 on 2018/9/1
 */
public class ImmutableJsonIter implements Iterator<Map.Entry<String, Object>> {

    final Iterator<Map.Entry<String, Object>> mapIter;

    public ImmutableJsonIter(Iterator<Map.Entry<String, Object>> mapIter) {
        this.mapIter = mapIter;
    }

    @Override
    public boolean hasNext() {
        return mapIter.hasNext();
    }

    @Override
    public Map.Entry<String, Object> next() {
        Map.Entry<String, Object> entry = mapIter.next();
        if (entry.getValue() instanceof Map) {
            return new Entry(entry.getKey(), new ImmutableJson((Map)entry.getValue()));

        } else if(entry.getValue() instanceof JsonObject) {
            return new Entry(entry.getKey(), new ImmutableJson((JsonObject)entry.getValue()));

        } else if (entry.getValue() instanceof List) {
            return new Entry(entry.getKey(), new ImmutableJsonArray((List) entry.getValue()));

        } else if(entry.getValue() instanceof JsonArray) {

            return new Entry(entry.getKey(), new ImmutableJsonArray((JsonArray) entry.getValue()));
        }

        CheckUtil.checkAndCopy(entry.getValue(), false);
        return entry;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("immutable json");
    }


private static final class Entry implements Map.Entry<String, Object> {
    final String key;
    final Object value;

    public Entry(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public Object setValue(Object value) {
        throw new UnsupportedOperationException();
    }
}
}
