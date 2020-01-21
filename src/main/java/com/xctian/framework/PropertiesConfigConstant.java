package com.xctian.framework;

/**
 * 提供simple.properties配置文件中的常量
 *
 * @author xctian
 * @date 2020/1/17
 */
public interface PropertiesConfigConstant {

    String CONFIG_FILE_NAME = "simple.properties";

    String JDBC_DRIVER = "simple.framework.jdbc.driver";
    String JDBC_URL = "simple.framework.jdbc.url";
    String JDBC_USERNAME = "simple.framework.jdbc.username";
    String JDBC_PASSWORD = "simple.framework.jdbc.password";

    String APP_BASE_PACKAGE = "simple.framework.app.base_package";
    String APP_JSP_PATH = "simple.framework.app.jsp_path";
    String APP_ASSET_PATH = "simple.framework.app.asset_path";
}
