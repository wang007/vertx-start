package me.wang007.router.delegate;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.*;
import io.vertx.ext.web.Route;

import java.util.List;

/**
 * 为Route设置pathPrefix
 *
 * Created by wang007 on 2018/8/22.
 */
public class DelegateRoute implements io.vertx.ext.web.Route {

    private Route delegate ;

    /**
     * 最后添加在{@link io.vertx.ext.web.Route}中的path前缀
     */
    private String pathPrefix ;


    public DelegateRoute(Route route) {
        this.delegate = route ;
    }

    /**
     * {@link me.wang007.annotation.Route#value()}中的路径
     * @param pathPrefix 前缀
     * @return this
     */
    public DelegateRoute setPathPrefix(String pathPrefix) {
        this.pathPrefix = pathPrefix ;
        return this;
    }

    public String getPathPrefix() {
        return this.pathPrefix ;
    }

    private String getFullPath(String path) {
        String newPath = RouteUtils.checkPath(path);
        return this.getPathPrefix() != null && this.pathPrefix != null? this.pathPrefix + newPath: newPath;
    }

    @Override
    public Route method(HttpMethod method) {
        return delegate.method(method);
    }

    @Override
    public Route path(String path) {
        return delegate.path(getFullPath(path));
    }

    @Override
    public Route pathRegex(String path) {
        return delegate.pathRegex(getFullPath(path));
    }

    @Override
    public Route produces(String contentType) {
        return delegate.produces(contentType);
    }

    @Override
    public Route consumes(String contentType) {
        return delegate.consumes(contentType);
    }

    @Override
    public Route order(int order) {
        return delegate.order(order);
    }

    @Override
    public Route last() {
        return delegate.last();
    }

    @Override
    public Route handler(Handler<RoutingContext> requestHandler) {
        return delegate.handler(requestHandler);
    }

    @Override
    public Route blockingHandler(Handler<RoutingContext> requestHandler) {
        return delegate.blockingHandler(requestHandler);
    }

    @Override
    public Route blockingHandler(Handler<RoutingContext> requestHandler, boolean ordered) {
        return delegate.blockingHandler(requestHandler, ordered);
    }

    @Override
    public Route failureHandler(Handler<RoutingContext> failureHandler) {
        return delegate.failureHandler(failureHandler);
    }

    @Override
    public Route remove() {
        return delegate.remove();
    }

    @Override
    public Route disable() {
        return delegate.disable();
    }

    @Override
    public Route enable() {
        return delegate.enable();
    }

    @Override
    public Route useNormalisedPath(boolean useNormalisedPath) {
        return delegate.useNormalisedPath(useNormalisedPath);
    }

    @Override
    public String getPath() {
        return delegate.getPath();
    }

    @Override
    public Route setRegexGroupsNames(List<String> groups) {
        return delegate.setRegexGroupsNames(groups);
    }
}
