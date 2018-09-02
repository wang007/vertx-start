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
 * copy from vert.x JsonArray
 *
 * created by wang007 on 2018/9/1
 */
public class ImmutableJsonArrayIter implements Iterator<Object> {


    final Iterator<Object> listIter;

    public ImmutableJsonArrayIter(Iterator<Object> listIter) {
        this.listIter = listIter;
    }

    @Override
    public boolean hasNext() {
        return listIter.hasNext();
    }

    @Override
    public Object next() {
        Object val = listIter.next();
        if (val instanceof Map) {
            val = new ImmutableJson((Map) val);

        } else if(val instanceof JsonObject) {
            val = new ImmutableJson((JsonObject) val);

        } else if (val instanceof List) {
            val = new ImmutableJsonArray((List) val);

        } else if (val instanceof JsonArray) {
            val = new ImmutableJsonArray((JsonArray) val);
        }
        CheckUtil.checkAndCopy(val, false);
        return val;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("immutable jsonArray");
    }

}
