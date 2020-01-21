package com.xctian.framework.utils;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.Collection;
import java.util.Map;

/**
 * 集合工具类
 *
 * @author xctian
 * @date 2020/1/17
 */
public class CollectionUtil {

    /**
     * 判断Collection是否为空
     *
     * @param collection 待判断的集合
     * @return 判断结果
     */
    public static boolean isEmpty(Collection<?> collection) {
        return CollectionUtils.isEmpty(collection);
    }

    /**
     * 判断Collection是否非空
     *
     * @param collection 待判断的集合
     * @return 判断结果
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    /**
     * 判断Map是否为空
     *
     * @param map 待判断的Map
     * @return 判断结果
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return MapUtils.isEmpty(map);
    }

    /**
     * 判断Map是否非空
     *
     * @param map 待判断的Map
     * @return 判断结果
     */
    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

}
