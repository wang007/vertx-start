package org.wang007.test;

import io.vertx.core.AbstractVerticle;
import org.wang007.annotation.*;
import org.wang007.test.demo.DemoRouter;


/**
 * created by wang007 on 2018/8/25
 */
@Deploy(instances = 8, worker = true, multiThreaded = true)
//@Component
//@ConfigurationProperties(prefix = "mysql.datasource")

public class A1 extends AbstractVerticle {

    @Value("person.name")
    @Ann1
    @Ann2
    private String name;

    @Value("person.name1")
    public String name1 ;

    @Value("person.name2")
    protected String name2;

    @Value("person.name3")
    String name3 ;

    @Inject("demoRouter")
    @Ann1
    @Ann2
    private DemoRouter router;

    @Inject("inject1")
    public DemoRouter router1;

    @Inject
    protected DemoRouter router2;

    private static String woshistatic;



}
