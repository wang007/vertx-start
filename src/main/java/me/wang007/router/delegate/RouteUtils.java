package me.wang007.router.delegate;

import me.wang007.utils.StringUtils;

/**
 *
 *
 * Created by wang007 on 2018/8/23.
 */
public class RouteUtils {

    /**
     * 检查路径， 允许path == ""， 但path != null时，path一定要以 “/” 开头
     *
     * @param path
     * @return
     */
    public static String checkPath(String path) {
        String path1 = StringUtils.trimOrThrow(path, "path不能为空");
        if("".equals(path1)) return "" ;

        if (path1.charAt(0) != '/') {
            throw new IllegalArgumentException("Path must start with /");
        }
        return path1;
    }

}
