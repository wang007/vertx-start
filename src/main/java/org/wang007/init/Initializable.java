package org.wang007.init;

import io.vertx.core.Vertx;

/**
 * 可初始化的Component, 使用该接口做初始化工作
 *
 * created by wang007 on 2018/8/22
 */
public interface Initializable {

    void initial(Vertx vertx);
}
