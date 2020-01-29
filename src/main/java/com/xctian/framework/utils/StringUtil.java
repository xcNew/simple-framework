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
     * 字符串分隔符
     */
    public static final String SEPARATOR = String.valueOf((char) 29);

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

    /**
     * 分割固定格式字符串
     */
    public static String[] splitString(String str,String separator){
        return StringUtils.splitByWholeSeparator(str,separator);
    }

}
