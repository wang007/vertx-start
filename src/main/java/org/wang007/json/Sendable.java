package org.wang007.json;

/**
 * eventBus send json的时候，会发生一次copy，这里
 *
 * created by wang007 on 2018/9/1
 */
public interface Sendable {


    /**
     *
     * @return true: sendable的实现类已经发送 对象将变得不可变  false: mutable
     */
    boolean isSend();

    /**
     * 设置该Sendable已经经过eventBus send,  那么之后，该对象将 immutable
     *
     * 由实现类保证 eventBus send之后的immutable
     *
     */
    void send();

}
