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


    /**
     * 拓展route协程handler
     */
    suspend fun Route.coHandler(handler: suspend (RoutingContext) -> Unit): Route = this.handler {
        launch { handler(it) }
    }


    /**
     * 拓展Router的协程方法
     *
     */
    suspend fun Router.route(path: String, handler: suspend (RoutingContext) -> Unit): Route {
        return this.route(path).coHandler(handler)
    }
    suspend fun Router.get(path: String, handler: suspend (RoutingContext) -> Unit): Route {
        return this.get(path).coHandler(handler)
    }
    suspend fun Router.post(path: String, handler: suspend (RoutingContext) -> Unit): Route {
        return this.post(path).coHandler(handler)
    }
    suspend fun Router.put(path: String, handler: suspend (RoutingContext) -> Unit): Route {
        return this.put(path).coHandler(handler)
    }
    suspend fun Router.patch(path: String, handler: suspend (RoutingContext) -> Unit): Route {
        return this.patch(path).coHandler(handler)
    }
    suspend fun Router.delete(path: String, handler: suspend (RoutingContext) -> Unit): Route {
        return this.delete(path).coHandler(handler)
    }

}