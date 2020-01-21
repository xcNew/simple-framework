package com.xctian.framework.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 配置文件工具类
 *
 * @author xctian
 * @date 2020/1/17
 */
public class PropsUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(PropsUtil.class);

    /**
     * 加载属性文件，获取Properties对象
     *
     * @param fileName 属性文件名
     * @return Properties
     */
    public static Properties loadProps(String fileName) {
        Properties props = null;
        InputStream is = null;
        try {
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
            if (null == is) {
                throw new FileNotFoundException(fileName + "file is not found");
            }
            props = new Properties();
            props.load(is);
        } catch (IOException e) {
            LOGGER.error("加载配置文件失败", e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    LOGGER.error("inputstream关闭失败", e);
                }
            }
        }
        return props;
    }

    /**
     * 从Properties对象获取字符串属性
     *
     * @param properties properties对象
     * @param key        属性关键字
     * @return 对应的字符串属性
     */
    public static String getString(Properties properties, String key) {
        return getString(properties, key, "");
    }

    /**
     * 从Properties对象获取字符串属性,可指定默认值
     *
     * @param properties   properties对象
     * @param key          属性关键字
     * @param defaultValue 默认值
     * @return 对应的字符串属性
     */
    public static String getString(Properties properties, String key, String defaultValue) {
        String value = defaultValue;
        if (properties.contains(key)) {
            value = properties.getProperty(key);
        }
        return value;
    }

    /**
     * 从Properties对象获取int型属性,需经过CastUtil进行数据转型
     *
     * @param properties properties对象
     * @param key        属性关键字
     * @return 对应的int属性
     */
    public static int getInt(Properties properties, String key) {
        return getInt(properties, key, 0);
    }

    /**
     * 从Properties对象获取int型属性,可指定默认值
     *
     * @param properties   properties对象
     * @param key          属性关键字
     * @param defaultValue 默认值
     * @return 对应的int属性
     */
    public static int getInt(Properties properties, String key, int defaultValue) {
        int value = defaultValue;
        if (properties.contains(key)) {
            value = CastUtil.castToInt(properties.getProperty(key));
        }
        return value;
    }


    /**
     * 从Properties对象获取double型属性,需经过CastUtil进行数据转型
     *
     * @param properties properties对象
     * @param key        属性关键字
     * @return 对应的double属性
     */
    public static double getDouble(Properties properties, String key) {
        return getDouble(properties, key, 0);
    }

    /**
     * 从Properties对象获取double型属性,可指定默认值
     *
     * @param properties   properties对象
     * @param key          属性关键字
     * @param defaultValue 默认值
     * @return 对应的double属性
     */
    public static double getDouble(Properties properties, String key, double defaultValue) {
        double value = defaultValue;
        if (properties.contains(key)) {
            value = CastUtil.castToDouble(properties.getProperty(key));
        }
        return value;
    }

    /**
     * 从Properties对象获取boolean型属性,需经过CastUtil进行数据转型
     *
     * @param properties properties对象
     * @param key        属性关键字
     * @return 对应的boolean属性
     */
    public static boolean getBoolean(Properties properties, String key) {
        return getBoolean(properties, key, false);
    }

    /**
     * 从Properties对象获取boolean型属性,可指定默认值
     *
     * @param properties   properties对象
     * @param key          属性关键字
     * @param defaultValue 默认值
     * @return 对应的boolean属性
     */
    public static boolean getBoolean(Properties properties, String key, boolean defaultValue) {
        boolean value = defaultValue;
        if (properties.contains(key)) {
            value = CastUtil.castToBoolean(properties.getProperty(key));
        }
        return value;
    }
}
