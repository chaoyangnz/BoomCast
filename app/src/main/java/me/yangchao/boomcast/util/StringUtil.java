package me.yangchao.boomcast.util;

/**
 * Created by richard on 3/11/17.
 */

public final class StringUtil {

    public static String trim(String str, int maxLengh) {
        if(str.length() <= maxLengh) return str;

        return str.substring(0, maxLengh-2) + "..";
    }

    public final static int DEFAULT_TRIM_LENGTH = 30;

    public static String trim(String str) {
        return trim(str, DEFAULT_TRIM_LENGTH);
    }
}
