package com.xctian.framework.helper;

import com.xctian.framework.PropertiesConfigConstant;
import com.xctian.framework.utils.PropsUtil;

import java.util.Properties;

/**
 * 配置文件读取工具类
 *
 * @author xctian
 * @date 2020/1/17
 */
public class PropertiesConfigHelper {

    private static final Properties CONFIG_PROPERTIES = PropsUtil.loadProps(PropertiesConfigConstant.CONFIG_FILE_NAME);

    /**
     * 获取JDBC驱动
     */
    public static String getJdbcDriver() {
        return PropsUtil.getString(CONFIG_PROPERTIES, PropertiesConfigConstant.JDBC_DRIVER);
    }

    /**
     * 获取JDBC URL
     */
    public static String getJdbcUrl() {
        return PropsUtil.getString(CONFIG_PROPERTIES, PropertiesConfigConstant.JDBC_URL);
    }

    /**
     * 获取JDBC用户名
     */
    public static String getJdbcUserName() {
        return PropsUtil.getString(CONFIG_PROPERTIES, PropertiesConfigConstant.JDBC_USERNAME);
    }

    /**
     * 获取JDBC密码
     */
    public static String getJdbcPassword() {
        return PropsUtil.getString(CONFIG_PROPERTIES, PropertiesConfigConstant.JDBC_PASSWORD);
    }

    /**
     * 获取应用基础包名
     */
    public static String getAppBasePackage() {
        return PropsUtil.getString(CONFIG_PROPERTIES, PropertiesConfigConstant.APP_BASE_PACKAGE);
    }

    /**
     * 获取应用JSP路径,有缺省配置
     */
    public static String getAppJspPath() {
        return PropsUtil.getString(CONFIG_PROPERTIES, PropertiesConfigConstant.APP_JSP_PATH, "/WEB-INF/view/");
    }

    /**
     * 获取应用静态资源路径，有缺省配置
     */
    public static String getAppAssetPath() {
        return PropsUtil.getString(CONFIG_PROPERTIES, PropertiesConfigConstant.APP_ASSET_PATH, "/asset/");
    }

}
