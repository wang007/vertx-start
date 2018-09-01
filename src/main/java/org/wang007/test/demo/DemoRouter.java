package org.wang007.test.demo;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import org.wang007.annotation.Inject;
import org.wang007.annotation.root.RootForComponent;
import org.wang007.annotation.Route;
import org.wang007.constant.PropertyConst;
import org.wang007.router.LoadRouter;

import java.lang.annotation.Annotation;

/**
 *
 * Created by wang007 on 2018/8/21.
 */
@Route(value = "/api")
public class DemoRouter implements LoadRouter {

    @Inject
    private PropertyConst propertyConst ;

    @Override
    public void start(Router router, Vertx vertx) {

        router.post("/add").handler(rc -> {

        });

        router.get("/query").handler(rc -> {

        });


/*
        AnnotatedType a = this.getClass().getAnnotatedSuperclass();
        Annotation[] annotations = a.getAnnotations();
        System.out.println(annotations);
        System.out.println(a);
*/


        Annotation[] annotations = this.getClass().getAnnotations();
        for (Annotation a : annotations) {
            Class<? extends Annotation> aClass = a.annotationType();
            RootForComponent annotations1 = aClass.getAnnotation(RootForComponent.class);
            System.out.println(annotations1);

            Annotation[] a1 = a.getClass().getDeclaredAnnotations();
            System.out.println("a1 -> " + a1);
            Annotation[] a2 = a.getClass().getAnnotations();
            System.out.println("a2 -> " + a2);
        }


    }

    public static void main(String[] args) {

        String name = DemoRouter.class.getName();
        System.out.println("name: -> " + name);

        String canonicalName = DemoRouter.class.getCanonicalName();
        System.out.println("canonicalName -> " + canonicalName);

        String simpleName = DemoRouter.class.getSimpleName();
        System.out.println("simpleName -> " + simpleName);

        String typeName = DemoRouter.class.getTypeName();
        System.out.println("typeName -> " + typeName);


        Vertx vertx = Vertx.vertx();
        Router router = Router.router(vertx);
        DemoRouter demoRouter = new DemoRouter();
        demoRouter.start(router, vertx);

    }
}
