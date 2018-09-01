package org.wang007.test;

import org.wang007.annotation.Component;
import org.wang007.annotation.Deploy;
import org.wang007.annotation.Route;
import org.wang007.annotation.Scope;

/**
 * created by wang007 on 2018/8/23
 */
@Component
@Scope(scopePolicy = Scope.Policy.Prototype)
public class Aaa {
}
