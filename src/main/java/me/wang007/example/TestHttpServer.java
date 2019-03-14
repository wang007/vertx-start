package me.wang007.example;

import me.wang007.annotation.Deploy;
import me.wang007.verticle.HttpServerVerticle;

/**
 * me.wang007.example & test
 *
 * created by wang007 on 2019/2/27
 */
@Deploy(instances = Integer.MAX_VALUE)
public class TestHttpServer extends HttpServerVerticle {}
