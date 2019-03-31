package com.chengxuunion.invoketrace.common.util;

import com.alibaba.druid.pool.DruidDataSource;
import com.chengxuunion.util.database.NotPersistent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @Author youpanpan
 * @Description:
 * @Date:创建时间: 2018-12-14 17:28
 * @Modified By:
 */
public class JdbcUtils {

    private static final Logger logger = LoggerFactory.getLogger(JdbcUtils.class);

    private static final String MYSQL_URL_PREFIX = "jdbc:mysql";
    private static final String ORACLE_URL_PREFIX = "jdbc:oracle";

    private String driverClassName = "com.mysql.jdbc.Driver";

    private String url;

    private String userName;

    private String password;

    private Connection conn;
    private PreparedStatement statement;

    private static DruidDataSource dataSource = null;

    static {
        try {
            if (InvokeTracePropertiesUtils.getInstance() != null && InvokeTracePropertiesUtils.getInstance().getValue("url") != null) {
                dataSource = new DruidDataSource();
                dataSource.setUrl(InvokeTracePropertiesUtils.getInstance().getValue("url"));
                dataSource.setUsername(InvokeTracePropertiesUtils.getInstance().getValue("username"));
                dataSource.setPassword(InvokeTracePropertiesUtils.getInstance().getValue("password"));
                dataSource.setDriverClassName(InvokeTracePropertiesUtils.getInstance().getValue("driver-class-name"));
                dataSource.setFilters(InvokeTracePropertiesUtils.getInstance().getValue("filters"));
                dataSource.setValidationQuery(InvokeTracePropertiesUtils.getInstance().getValue("validationQuery"));
                dataSource.setMaxActive(InvokeTracePropertiesUtils.getInstance().getIntValue("maxActive"));
                dataSource.setInitialSize(InvokeTracePropertiesUtils.getInstance().getIntValue("initialSize"));
                dataSource.setMaxWait(Long.parseLong(InvokeTracePropertiesUtils.getInstance().getValue("maxWait")));
                dataSource.setMinIdle(InvokeTracePropertiesUtils.getInstance().getIntValue("minIdle"));
                dataSource.setTimeBetweenConnectErrorMillis(Long.parseLong(InvokeTracePropertiesUtils.getInstance().getValue("timeBetweenEvictionRunsMillis")));
                dataSource.setMinEvictableIdleTimeMillis(Long.parseLong(InvokeTracePropertiesUtils.getInstance().getValue("minEvictableIdleTimeMillis")));
                dataSource.setTestWhileIdle(InvokeTracePropertiesUtils.getInstance().getBoolValue("testWhileIdle"));
                dataSource.setTestOnBorrow(InvokeTracePropertiesUtils.getInstance().getBoolValue("testOnBorrow"));
                dataSource.setTestOnReturn(InvokeTracePropertiesUtils.getInstance().getBoolValue("testOnReturn"));
                dataSource.setPoolPreparedStatements(InvokeTracePropertiesUtils.getInstance().getBoolValue("poolPreparedStatements"));
                dataSource.setMaxPoolPreparedStatementPerConnectionSize(InvokeTracePropertiesUtils.getInstance().getIntValue("maxOpenPreparedStatements"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public JdbcUtils() {

    }

    public JdbcUtils(String driverClassName, String url, String userName, String password) {
        this.driverClassName = driverClassName;
        this.url = url;
        this.userName = userName;
        this.password = password;

        try {
            Class.forName(this.driverClassName);
        } catch (ClassNotFoundException e) {
            logger.error("加载驱动类:" + driverClassName + "发生异常");
        }
    }

    /**
     * 获取数据库连接对象
     *
     * @return
     */
    public Connection getConnection() {
        try {
            if (dataSource != null) {
                conn = dataSource.getConnection();
            } else {
                conn = DriverManager.getConnection(this.url, this.userName, this.password);
            }
        } catch (SQLException e) {
            logger.error("获取数据库连接【"+ this.url +", "+ this.userName +"】发生异常", e);
            throw new RuntimeException("数据库连接异常【"+ this.url + "," + this.userName + "," + this.password +"】");
        }
        return conn;
    }

    /**
     * 查询列表
     *
     * @param sql   sql语句
     * @param paramList sql语句参数列表
     * @param clazz 反射的类型
     * @param <T>
     * @return  列表
     */
    public <T> List<T> selectList(String sql, List<Object> paramList, Class<T> clazz) {
        if (conn == null) {
            getConnection();
        }
        List<T> list = new ArrayList<>();

        try {
            statement = conn.prepareStatement(sql);
            int paramIndex = 1;
            for (Object param : paramList) {
                statement.setObject(paramIndex, param);
                paramIndex++;
            }

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()) {
                T obj = clazz.newInstance();
                Field[] fields = obj.getClass().getDeclaredFields();
                for (Field field : fields) {
                    NotPersistent notPersistent = field.getDeclaredAnnotation(NotPersistent.class);
                    if (notPersistent == null) {
                        field.setAccessible(true);
                        field.set(obj, resultSet.getObject(field.getName()));
                    }
                }
                list.add(obj);
            }
            resultSet.close();
        } catch (Exception e) {
            logger.error("执行sql【" + sql + "】发生异常", e);
            throw new RuntimeException(e.getMessage(), e);
        }

        return list;
    }

    /**
     * 查询单个对象
     *
     * @param sql   sql语句
     * @param paramList sql语句参数列表
     * @param clazz 反射的类型
     * @param <T>
     * @return  单个对象
     */
    public <T> T selectOne(String sql, List<Object> paramList, Class<T> clazz) {
        if (conn == null) {
            getConnection();
        }
        T obj = null;

        try {
            statement = conn.prepareStatement(sql);
            int paramIndex = 1;
            for (Object param : paramList) {
                statement.setObject(paramIndex, param);
                paramIndex++;
            }

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()) {
                obj = clazz.newInstance();
                Field[] fields = obj.getClass().getDeclaredFields();
                for (Field field : fields) {
                    NotPersistent notPersistent = field.getDeclaredAnnotation(NotPersistent.class);
                    if (notPersistent == null) {
                        field.setAccessible(true);
                        field.set(obj, resultSet.getObject(field.getName()));
                    }
                }
            }
            resultSet.close();
        } catch (Exception e) {
            logger.error("执行sql【" + sql + "】发生异常", e);
            throw new RuntimeException(e.getMessage(), e);
        }

        return obj;
    }

    /**
     * 执行sql
     *
     * @param sql
     * @param paramList
     * @return
     */
    public boolean execute(String sql, List<Object> paramList) {
        if (conn == null) {
            getConnection();
        }

        try {
            statement = conn.prepareStatement(sql);
            int paramIndex = 1;
            for (Object param : paramList) {
                statement.setObject(paramIndex, param);
                paramIndex++;
            }

            return statement.execute();
        } catch (Exception e) {
            logger.error("执行sql【" + sql + "】发生异常", e);
            return false;
        }
    }

    /**
     * 关闭连接
     */
    public void close() {
        try {
            if (statement != null) {
                statement.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            logger.error("关闭数据库连接发生异常", e);
        }
    }

    /**
     * 根据URL获取驱动类名
     *
     * @param url   数据库连接URL
     * @return  驱动类名
     */
    public static String getDriverClassName(String url) {
        if (url.startsWith(MYSQL_URL_PREFIX)) {
            return "com.mysql.jdbc.Driver";
        } else if (url.startsWith(ORACLE_URL_PREFIX)) {
            return "oracle.jdbc.driver.OracleDriver";
        }

        return "";
    }
}
