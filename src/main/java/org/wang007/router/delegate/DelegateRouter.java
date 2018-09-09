package org.wang007.router.delegate;


import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.impl.RouterImpl;
import org.wang007.annotation.Route;


import java.util.List;

/**
 * 代理{@link RouterImpl} 给{@link io.vertx.ext.web.Route} 添加前缀
 * <p>
 * Created by wang007 on 2018/8/21.
 */
public class DelegateRouter implements Router {

    private Router delegate;

    /**
     * 最后添加在{@link io.vertx.ext.web.Route}中的path前缀
     */
    private String pathPrefix;

    /**
     * 挂载路径
     * "" 或 "/" 挂载到 mainRouter上
     */
    private String mountPath;


    public DelegateRouter(Router router) {
        this.delegate = router;
    }

    /**
     * {@link Route#value()}中的路径
     *
     * @param pathPrefix
     * @return
     */
    public DelegateRouter setPathPrefix(String pathPrefix) {
        this.pathPrefix = pathPrefix;
        return this;
    }

    public String getPathPrefix() {
        return this.pathPrefix;
    }

    public Router getDelegate() {
        return delegate;
    }

    public String getMountPath() {
        return mountPath;
    }

    public DelegateRouter setMountPath(String mountPath) {
        this.mountPath = mountPath;
        return this;
    }

    private String getFullPath(String path) {
        String newPath = RouteUtils.checkPath(path);
        return this.getPathPrefix() != null && this.pathPrefix != null ? this.pathPrefix + newPath : newPath;
    }


    @Override
    public void accept(HttpServerRequest request) {
        delegate.accept(request);
    }

    //================ route start =============================


    @Override
    public io.vertx.ext.web.Route route() {
        return new DelegateRoute(delegate.route()).setPathPrefix(this.pathPrefix);
    }

    @Override
    public io.vertx.ext.web.Route route(HttpMethod method, String path) {
        return new DelegateRoute(delegate.route(method, getFullPath(path))).setPathPrefix(this.pathPrefix);
    }

    @Override
    public io.vertx.ext.web.Route route(String path) {
        return new DelegateRoute(delegate.route(getFullPath(path))).setPathPrefix(this.pathPrefix);
    }

    @Override
    public io.vertx.ext.web.Route routeWithRegex(HttpMethod method, String regex) {
        return new DelegateRoute(delegate.routeWithRegex(method, getFullPath(regex))).setPathPrefix(this.pathPrefix);
    }

    @Override
    public io.vertx.ext.web.Route routeWithRegex(String regex) {
        return new DelegateRoute(delegate.routeWithRegex(getFullPath(regex))).setPathPrefix(this.pathPrefix);
    }

    //================ route end =============================

    @Override
    public io.vertx.ext.web.Route get() {
        return delegate.get();
    }

    @Override
    public io.vertx.ext.web.Route get(String path) {
        return delegate.get(getFullPath(path));
    }

    @Override
    public io.vertx.ext.web.Route getWithRegex(String regex) {
        return delegate.getWithRegex(getFullPath(regex));
    }

    @Override
    public io.vertx.ext.web.Route head() {
        return delegate.head();
    }

    @Override
    public io.vertx.ext.web.Route head(String path) {
        return delegate.head(getFullPath(path));
    }

    @Override
    public io.vertx.ext.web.Route headWithRegex(String regex) {
        return delegate.headWithRegex(getFullPath(regex));
    }

    @Override
    public io.vertx.ext.web.Route options() {
        return delegate.options();
    }

    @Override
    public io.vertx.ext.web.Route options(String path) {
        return delegate.options(getFullPath(path));
    }

    @Override
    public io.vertx.ext.web.Route optionsWithRegex(String regex) {
        return delegate.optionsWithRegex(getFullPath(regex));
    }

    @Override
    public io.vertx.ext.web.Route put() {
        return delegate.put();
    }

    @Override
    public io.vertx.ext.web.Route put(String path) {
        return delegate.put(getFullPath(path));
    }

    @Override
    public io.vertx.ext.web.Route putWithRegex(String regex) {
        return delegate.putWithRegex(getFullPath(regex));
    }

    @Override
    public io.vertx.ext.web.Route post() {
        return delegate.post();
    }

    @Override
    public io.vertx.ext.web.Route post(String path) {
        return delegate.post(getFullPath(path));
    }

    @Override
    public io.vertx.ext.web.Route postWithRegex(String regex) {
        return delegate.postWithRegex(getFullPath(regex));
    }

    @Override
    public io.vertx.ext.web.Route delete() {
        return delegate.delete();
    }

    @Override
    public io.vertx.ext.web.Route delete(String path) {
        return delegate.delete(getFullPath(path));
    }

    @Override
    public io.vertx.ext.web.Route deleteWithRegex(String regex) {
        return delegate.deleteWithRegex(getFullPath(regex));
    }

    @Override
    public io.vertx.ext.web.Route trace() {
        return delegate.trace();
    }

    @Override
    public io.vertx.ext.web.Route trace(String path) {
        return delegate.trace(getFullPath(path));
    }

    @Override
    public io.vertx.ext.web.Route traceWithRegex(String regex) {
        return delegate.traceWithRegex(getFullPath(regex));
    }

    @Override
    public io.vertx.ext.web.Route connect() {
        return delegate.connect();
    }

    @Override
    public io.vertx.ext.web.Route connect(String path) {
        return delegate.connect(getFullPath(path));
    }

    @Override
    public io.vertx.ext.web.Route connectWithRegex(String regex) {
        return delegate.connectWithRegex(getFullPath(regex));
    }

    @Override
    public io.vertx.ext.web.Route patch() {
        return delegate.patch();
    }

    @Override
    public io.vertx.ext.web.Route patch(String path) {
        return delegate.patch(getFullPath(path));
    }

    @Override
    public io.vertx.ext.web.Route patchWithRegex(String regex) {
        return delegate.patchWithRegex(getFullPath(regex));
    }

    @Override
    public List<io.vertx.ext.web.Route> getRoutes() {
        return delegate.getRoutes();
    }

    @Override
    public Router clear() {
        throw new UnsupportedOperationException("unsupported clear");
    }

    @Override
    public Router mountSubRouter(String mountPoint, Router subRouter) {
        return delegate.mountSubRouter(mountPoint, subRouter);
    }

    @Override
    public Router exceptionHandler(Handler<Throwable> exceptionHandler) {
        return delegate.exceptionHandler(exceptionHandler);
    }

    @Override
    public void handleContext(RoutingContext context) {
        delegate.handleContext(context);
    }

    @Override
    public void handleFailure(RoutingContext context) {
        delegate.handleFailure(context);
    }


}
