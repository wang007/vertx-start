package me.wang007.router

import io.vertx.core.Vertx
import io.vertx.ext.web.Route
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.wang007.router.LoadRouter
import kotlin.coroutines.CoroutineContext

/**
 *  实现kt协程的LoadRouter
 *
 *  created by wang007 on 2019/2/28
 */
abstract class CoroutineRouter : LoadRouter, CoroutineScope {

    protected lateinit var vertx: Vertx

    override val coroutineContext: CoroutineContext by lazy { vertx.dispatcher() }

    final override fun init(router: Router, vertx: Vertx) {
        this.vertx = vertx
        doInit(router, vertx)
    }

    final override fun start(router: Router, vertx: Vertx) {
        launch { doStart(router, vertx) }
    }

    /**
     * 子类进行初始化
     */
    protected open fun doInit(router: Router, vertx: Vertx) {}

    protected abstract suspend fun doStart(router: Router, vertx: Vertx)

    protected suspend fun Route.coHandler(handler: suspend (RoutingContext) -> Unit): Route = this.handler {
        launch { handler(it) }
    }
}