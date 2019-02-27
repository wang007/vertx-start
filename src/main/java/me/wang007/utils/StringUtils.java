package me.wang007.utils;

/**
 * Created by wang007 on 2018/8/23.
 */
public class StringUtils {

    /**
     * trim
     * if str == null, return “”
     *
     * @param str
     * @return
     */
    public static String trimToEmpty(String str) {
        return str == null ? "": str.trim();
    }

    /**
     * trim
     * if str == null,  throw exception
     *
     * @param str
     * @return
     */
    public static String trimOrThrow(String str, String msg) {
        if(str == null) throw new IllegalArgumentException(msg) ;
        return str.trim();
    }

    /**
     * trim
     * if str == null, return {@code defaultStr}
     *
     * @param str
     * @param defaultStr
     * @return
     */
    public static String trimOrElse(String str, String defaultStr) {
        if(str == null) return defaultStr ;
        return str.trim();
    }

    /**
     * @param str
     * @return str == null, str == "" 返回true， str == "   " 和其他情况，返回false
     */
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /**
     *
     * @param str
     * @return str == null, str == "" 返回 false， str == "   " 和 其他情况， 返回false
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     *
     * @param str
     * @return str == null, str == "", str == "   "返回true， 其他情况返回 false
     */
    public static boolean isBlank(String str) {
        if(isEmpty(str)) return true;
        int len = str.length();
        int st = 0;
        while ((st < len) && (str.charAt(st) <= ' ')) {
            st++;
        }
        return st == len ;
    }

    /**
     *
     * @param str
     * @return str == null, str == "", str == "   "返回false， 其他情况返回 true
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    public static boolean equals(String str1, String str2) {
        if (str1 == str2) {
            return true;
        }
        if (str1 == null || str2 == null) {
            return false;
        }
        if (str1.length() != str2.length()) {
            return false;
        }
        return str1.equals(str2) ;
    }

    /**
     * 把首字母大写的str 替换成 首字母小写的str
     *
     * @param str
     * @return
     */
    public static String replaceFirstUpperCase(String str) {
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }


}
