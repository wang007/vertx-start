package me.wang007.router

import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.ext.web.Route
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
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

    protected lateinit var router: Router

    override val coroutineContext: CoroutineContext by lazy { vertx.dispatcher() }

    final override fun init(router: Router, vertx: Vertx) {
        this.vertx = vertx
        this.router = router
        doInit(router, vertx)
    }

    final override fun start(future: Future<Void>) {
        launch {
            doStart()
            future.complete()
        }
    }

    /**
     * 子类进行初始化
     */
    protected open fun doInit(router: Router, vertx: Vertx) {}

    protected abstract suspend fun doStart()

    /**
     * 拓展route协程handler
     */
    suspend fun Route.coHandler(handler: suspend (RoutingContext) -> Unit): Route = handler { rc ->
        launch {
            try {
                handler(rc)
            } catch (e: Throwable) {
                rc.fail(e)
            }
        }
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