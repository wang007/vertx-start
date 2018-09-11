package org.wang007.examples.verticle;

import io.vertx.core.AbstractVerticle;
import org.wang007.annotation.Deploy;
import org.wang007.json.JsonSend;

/**
 * created by wang007 on 2018/9/11
 */
@Deploy
public class DemoVerticle extends AbstractVerticle {

    @Override
    public void start() throws Exception {

        vertx.eventBus().<JsonSend>consumer("json", msg -> {
            JsonSend body = msg.body();
            System.out.println(body);

            msg.reply(new JsonSend().put("name", "wang").put("age", 16));
        });
    }
}
