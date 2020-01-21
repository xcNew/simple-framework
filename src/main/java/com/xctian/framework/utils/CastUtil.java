package com.xctian.framework.utils;


/**
 * 数据转型操作工具类
 *
 * @author xctian
 * @date 2020/1/17
 */
public class CastUtil {

    /**
     * 将对象转换为String
     *
     * @param obj 待转换的对象
     * @return 转换后的String
     */
    public static String castToString(Object obj) {
        return CastUtil.castToString(obj, "");
    }

    /**
     * 将对象转换为String(提供默认值)
     *
     * @param obj          待转换的对象
     * @param defaultValue 默认值
     * @return 转换后的String
     */
    private static String castToString(Object obj, String defaultValue) {
        return obj != null ? String.valueOf(obj) : defaultValue;
    }

    /**
     * 将对象转换为doube
     *
     * @param obj 待转换的对象
     * @return 转换后的double
     */
    public static double castToDouble(Object obj) {
        return CastUtil.castToDouble(obj, 0);
    }

    /**
     * 将对象转换为double(提供默认值)
     *
     * @param obj          待转换的对象
     * @param defaultValue 默认值
     * @return 转换后的double
     */
    private static double castToDouble(Object obj, double defaultValue) {
        double doubleValue = defaultValue;
        if (obj != null) {
            String strValue = castToString(obj);
            if (StringUtil.isNotEmpty(strValue)) {
                try {
                    doubleValue = Double.parseDouble(strValue);
                } catch (NumberFormatException e) {
                    doubleValue = defaultValue;
                }
            }
        }
        return doubleValue;
    }

    /**
     * 将对象转换为long
     *
     * @param obj 待转换的对象
     * @return 转换后的long
     */
    public static long castToLong(Object obj) {
        return CastUtil.castToLong(obj, 0);
    }

    /**
     * 将对象转换为long(提供默认值)
     *
     * @param obj          待转换的对象
     * @param defaultValue 默认值
     * @return 转换后的long
     */
    private static long castToLong(Object obj, long defaultValue) {
        long longValue = defaultValue;
        if (obj != null) {
            String strValue = castToString(obj);
            if (StringUtil.isNotEmpty(strValue)) {
                try {
                    longValue = Long.parseLong(strValue);
                } catch (NumberFormatException e) {
                    longValue = defaultValue;
                }
            }
        }
        return longValue;
    }

    /**
     * 将对象转换为int
     *
     * @param obj 待转换的对象
     * @return 转换后的int
     */
    public static int castToInt(Object obj) {
        return CastUtil.castToInt(obj, 0);
    }

    /**
     * 将对象转换为int(提供默认值)
     *
     * @param obj          待转换的对象
     * @param defaultValue 默认值
     * @return 转换后的int
     */
    private static int castToInt(Object obj, int defaultValue) {
        int intValue = defaultValue;
        if (obj != null) {
            String strValue = castToString(obj);
            if (StringUtil.isNotEmpty(strValue)) {
                try {
                    intValue = Integer.parseInt(strValue);
                } catch (NumberFormatException e) {
                    intValue = defaultValue;
                }
            }
        }
        return intValue;
    }

    /**
     * 将对象转换为boolean
     *
     * @param obj 待转换的对象
     * @return 转换后的boolean
     */
    public static boolean castToBoolean(Object obj) {
        return CastUtil.castToBoolean(obj, false);
    }

    /**
     * 将对象转换为int(提供默认值)
     *
     * @param obj          待转换的对象
     * @param defaultValue 默认值
     * @return 转换后的boolean
     */
    private static boolean castToBoolean(Object obj, boolean defaultValue) {
        Boolean booleanValue = defaultValue;
        if (obj != null) {
            String strValue = castToString(obj);
            if (StringUtil.isNotEmpty(strValue)) {
                try {
                    booleanValue = Boolean.parseBoolean(strValue);
                } catch (NumberFormatException e) {
                    booleanValue = defaultValue;
                }
            }
        }
        return booleanValue;
    }


}
