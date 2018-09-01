package org.wang007.test;

import org.wang007.annotation.*;
import org.wang007.test.demo.DemoRouter;

/**
 * created by wang007 on 2018/8/27
 */
//@ConfigurationProperties(prefix = "mysql.datasource")
    @Component
    @Scope(scopePolicy = Scope.Policy.Prototype)
public class JdbcData {

    @Ann1
    private String username ;

    @Ann2
    private String password ;

    private int count;

    @Inject
    private DemoRouter demoRouter;

    @Value("shabi")
    private String foolish;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getFoolish() {
        return foolish;
    }

    public void setFoolish(String foolish) {
        this.foolish = foolish;
    }
}
