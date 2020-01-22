package com.xctian.framework.utils;

import org.apache.commons.lang3.ArrayUtils;

/**
 * 数组工具类
 *
 * @author xctian
 * @date 2020/1/22
 */
public class ArrayUtil {
    /**
     * 判断数组是否为空
     */
    public static boolean isEmpty(Object[] array){
        return ArrayUtils.isEmpty(array);
    }

    /**
     * 判断数组是否非空
     */
    public static boolean isNotEmpty(Object[] array){
        return !isEmpty(array);
    }
}
