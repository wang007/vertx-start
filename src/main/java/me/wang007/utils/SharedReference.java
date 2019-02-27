package me.wang007.utils;

import io.vertx.core.shareddata.Shareable;
import io.vertx.core.shareddata.SharedData;

/**
 * 用于{@link SharedData}中，对于{@link #ref}，使用者确保线程安全。
 *
 * created by wang007 on 2019/2/27
 */
public class SharedReference<V> implements Shareable {

    public final V ref;

    public SharedReference(V ref) {
        this.ref = ref;
    }

}
