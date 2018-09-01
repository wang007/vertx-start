package org.wang007.utils;

import java.util.Collection;

/**
 * created by wang007 on 2018/8/27
 */
public class CollectionUtils {


    /**
     * 判断集合是否为空，{@link java.util.Set}, {@link java.util.List} 可使用
     * @param collect
     * @return
     */
    public static boolean isEmpty(Collection<?> collect) {
        return collect == null || collect.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> collect) {
        return !isEmpty(collect);
    }

}
