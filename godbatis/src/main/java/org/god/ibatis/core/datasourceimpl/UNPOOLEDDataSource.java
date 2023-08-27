package org.god.ibatis.core.datasourceimpl;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

/**
 * ClassName: UNPOOLEDDataSource
 * PackageName: org.god.ibatis.core.datasourceimpl
 * Description:
 *              UNPOOLED数据源 不使用数据库连接池技术
 * @Author CuiBo
 * @Create 2023/8/27 12:23
 * @Version 1.0
 */
public class UNPOOLEDDataSource implements DataSource {

    private String driver;//数据库驱动
    private String url; //数据库url
    private String userName; //用户名
    private String password; //密码

    public UNPOOLEDDataSource() {
    }

    public UNPOOLEDDataSource(String driver, String url, String userName, String password) {
        try {
            Class.forName(driver);  //驱动只需要注册一次，所以直接在构造器内加载驱动
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        this.url = url;
        this.userName = userName;
        this.password = password;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url,userName,password);
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return null;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {

    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {

    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}
