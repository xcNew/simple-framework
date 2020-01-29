package com.xctian.framework.helper;

import com.xctian.framework.utils.CollectionUtil;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * 数据库操作助手类
 *
 * @author xctian
 * @date 2020/1/28
 */
public class DatabaseHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseHelper.class);

    private static final ThreadLocal<Connection> CONNECTION_HOLDER;

    private static final QueryRunner QUERY_RUNNER;

    private static final BasicDataSource DATA_SOURCE;

    static {
        CONNECTION_HOLDER = new ThreadLocal<Connection>();

        QUERY_RUNNER = new QueryRunner();

        DATA_SOURCE = new BasicDataSource();

        DATA_SOURCE.setDriverClassName(PropertiesConfigHelper.getJdbcDriver());

        DATA_SOURCE.setUrl(PropertiesConfigHelper.getJdbcUrl());

        DATA_SOURCE.setUsername(PropertiesConfigHelper.getJdbcUserName());

        DATA_SOURCE.setPassword(PropertiesConfigHelper.getJdbcPassword());
    }

    /**
     * 获取DataSource
     */
    public static DataSource getDataSource() {
        return DATA_SOURCE;
    }

    /**
     * 获取数据库连接,通过DBCP连接池
     */
    public static Connection getConnection() {
        Connection connection = CONNECTION_HOLDER.get();
        if (connection == null) {
            try {
                connection = DATA_SOURCE.getConnection();
            } catch (SQLException e) {
                LOGGER.error("get connection failure", e);
                throw new RuntimeException(e);
            } finally {
                CONNECTION_HOLDER.set(connection);
            }
        }
        return connection;
    }

    /**
     * 开启事务
     */
    public static void beginTransaction() {
        Connection conn = getConnection();
        if (conn != null) {
            try {
                conn.setAutoCommit(false);
            } catch (SQLException e) {
                LOGGER.error("事务开启失败", e);
                throw new RuntimeException(e);
            } finally {
                CONNECTION_HOLDER.set(conn);
            }
        }
    }

    /**
     * 提交事务
     */
    public static void commitTransaction() {
        Connection connection = getConnection();
        if (connection != null) {
            try {
                connection.commit();
                connection.close();
            } catch (SQLException e) {
                LOGGER.error("事务提交失败", e);
                throw new RuntimeException(e);
            } finally {
                CONNECTION_HOLDER.set(connection);
            }
        }
    }

    /**
     * 事务回滚
     */
    public static void rollbackTransaction() {
        Connection connection = getConnection();
        if (connection != null){
            try{
                connection.rollback();
                connection.close();
            }catch (SQLException e){
                LOGGER.error("事务回滚失败",e);
                throw new RuntimeException(e);
            }finally {
                CONNECTION_HOLDER.set(connection);
            }
        }
    }

    /**
     * 查询实体
     */
    public static <T> T queryEntity(Class<T> entityClass, String sql, Object... params) {
        T entity;
        try {
            Connection conn = getConnection();
            entity = QUERY_RUNNER.query(conn, sql, new BeanHandler<T>(entityClass), params);
        } catch (SQLException e) {
            LOGGER.error("query entity failure", e);
            throw new RuntimeException(e);
        }
        return entity;
    }

    /**
     * 查询实体列表
     */
    public static <T> List<T> queryEntityList(Class<T> entityClass,String sql,Object... params){
        List<T> entityList;
        try{
            Connection connection = getConnection();
            entityList = QUERY_RUNNER.query(connection,sql,new BeanListHandler<T>(entityClass),params);
        }catch (SQLException e){
            LOGGER.error("query entity list failure",e);
            throw new RuntimeException(e);
        }
        return entityList;
    }

    /**
     * 查询并返回单个列值
     */
    @SuppressWarnings("unchecked")
    public static <T> T query(String sql,Object...params){
        T obj;
        try{
            Connection connection = getConnection();
            obj = (T) QUERY_RUNNER.query(connection,sql,new ScalarHandler<>(),params);
        }catch (SQLException e){
            LOGGER.error("query failure",e);
            throw new RuntimeException(e);
        }
        return obj;
    }

    /**
     * 查询并返回多个列值
     */
    public static <T> List<T> queryList(String sql, Object... params) {
        List<T> list;
        try {
            Connection conn = getConnection();
            list = QUERY_RUNNER.query(conn, sql, new ColumnListHandler<T>(), params);
        } catch (SQLException e) {
            LOGGER.error("query list failure", e);
            throw new RuntimeException(e);
        }
        return list;
    }

    /**
     * 查询并返回多个列值（具有唯一性）
     */
    public static <T> Set<T> querySet(String sql, Object... params) {
        Collection<T> valueList = queryList(sql, params);
        return new LinkedHashSet<T>(valueList);
    }

    /**
     * 查询并返回数组
     */
    public static Object[] queryArray(String sql, Object... params) {
        Object[] resultArray;
        try {
            Connection conn = getConnection();
            resultArray = QUERY_RUNNER.query(conn, sql, new ArrayHandler(), params);
        } catch (SQLException e) {
            LOGGER.error("query array failure", e);
            throw new RuntimeException(e);
        }
        return resultArray;
    }

    /**
     * 查询并返回数组列表
     */
    public static List<Object[]> queryArrayList(String sql, Object... params) {
        List<Object[]> resultArrayList;
        try {
            Connection conn = getConnection();
            resultArrayList = QUERY_RUNNER.query(conn, sql, new ArrayListHandler(), params);
        } catch (SQLException e) {
            LOGGER.error("query array list failure", e);
            throw new RuntimeException(e);
        }
        return resultArrayList;
    }

    /**
     * 查询并返回结果集映射（列名 => 列值）
     */
    public static Map<String, Object> queryMap(String sql, Object... params) {
        Map<String, Object> resultMap;
        try {
            Connection conn = getConnection();
            resultMap = QUERY_RUNNER.query(conn, sql, new MapHandler(), params);
        } catch (SQLException e) {
            LOGGER.error("query map failure", e);
            throw new RuntimeException(e);
        }
        return resultMap;
    }

    /**
     * 查询并返回结果集映射列表（列名 => 列值）
     */
    public static List<Map<String, Object>> queryMapList(String sql, Object... params) {
        List<Map<String, Object>> resultMapList;
        try {
            Connection conn = getConnection();
            resultMapList = QUERY_RUNNER.query(conn, sql, new MapListHandler(), params);
        } catch (SQLException e) {
            LOGGER.error("query map list failure", e);
            throw new RuntimeException(e);
        }
        return resultMapList;
    }

    /**
     * 执行更新语句（包括：update、insert、delete）
     */
    public static int update(String sql, Object... params) {
        int rows;
        try {
            Connection conn = getConnection();
            rows = QUERY_RUNNER.update(conn, sql, params);
        } catch (SQLException e) {
            LOGGER.error("execute update failure", e);
            throw new RuntimeException(e);
        }
        return rows;
    }

    /**
     * 插入实体
     */
    public static <T> boolean insertEntity(Class<T> entityClass, Map<String, Object> fieldMap) {
        if (CollectionUtil.isEmpty(fieldMap)) {
            LOGGER.error("can not insert entity: fieldMap is empty");
            return false;
        }

        String sql = "INSERT INTO " + entityClass.getSimpleName();
        StringBuilder columns = new StringBuilder("(");
        StringBuilder values = new StringBuilder("(");
        for (String fieldName : fieldMap.keySet()) {
            columns.append(fieldName).append(", ");
            values.append("?, ");
        }
        columns.replace(columns.lastIndexOf(", "), columns.length(), ")");
        values.replace(values.lastIndexOf(", "), values.length(), ")");
        sql += columns + " VALUES " + values;

        Object[] params = fieldMap.values().toArray();

        return update(sql, params) == 1;
    }

    /**
     * 更新实体
     */
    public static <T> boolean updateEntity(Class<T> entityClass, long id, Map<String, Object> fieldMap) {
        if (CollectionUtil.isEmpty(fieldMap)) {
            LOGGER.error("can not update entity: fieldMap is empty");
            return false;
        }

        String sql = "UPDATE " + entityClass.getSimpleName() + " SET ";
        StringBuilder columns = new StringBuilder();
        for (String fieldName : fieldMap.keySet()) {
            columns.append(fieldName).append(" = ?, ");
        }
        sql += columns.substring(0, columns.lastIndexOf(", ")) + " WHERE id = ?";

        List<Object> paramList = new ArrayList<Object>();
        paramList.addAll(fieldMap.values());
        paramList.add(id);
        Object[] params = paramList.toArray();

        return update(sql, params) == 1;
    }

    /**
     * 删除实体
     */
    public static <T> boolean deleteEntity(Class<T> entityClass, long id) {
        String sql = "DELETE FROM " + entityClass.getSimpleName() + " WHERE id = ?";
        return update(sql, id) == 1;
    }
}
