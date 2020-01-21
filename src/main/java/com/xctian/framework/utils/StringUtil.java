package com.xctian.framework.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * 字符串工具类
 *
 * @author xctian
 * @date 2020/1/17
 */
public final class StringUtil {

    /**
     * 判断字符串是否为空
     *
     * @param str 目标字符串
     * @return 判断结果
     */
    public static boolean isEmpty(String str) {
        if (str != null) {
            // 删除了原始字符串头部和尾部的空格
            str.trim();
        }
        return StringUtils.isEmpty(str);
    }

    /**
     * 判断字符串是否非空
     *
     * @param str 目标字符串
     * @return 判断结果
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

}
