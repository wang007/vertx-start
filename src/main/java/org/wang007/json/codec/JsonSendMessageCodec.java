package org.wang007.json.codec;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import org.wang007.json.JsonSend;

/**
 * json zero copy on eventBus send
 *
 * //TODO register on {@link io.vertx.core.eventbus.EventBus#registerDefaultCodec(Class, MessageCodec)}
 *
 * created by wang007 on 2018/9/3
 */
public class JsonSendMessageCodec implements MessageCodec<JsonSend, JsonSend> {

    @Override
    public void encodeToWire(Buffer buffer, JsonSend json) {
        json.writeToBuffer(buffer);
        json.send();
    }

    @Override
    public JsonSend decodeFromWire(int pos, Buffer buffer) {
        JsonSend json = new JsonSend();
        json.readFromBuffer(pos, buffer);
        json.send();
        return json;
    }

    @Override
    public JsonSend transform(JsonSend json) {
        json.send();
        return json;
    }

    @Override
    public String name() {
        return JsonSend.Codec_Name;
    }

    @Override
    public byte systemCodecID() {
        return -1;
    }
}
