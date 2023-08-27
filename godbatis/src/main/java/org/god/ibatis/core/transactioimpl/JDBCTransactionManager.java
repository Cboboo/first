package org.god.ibatis.core.transactioimpl;

import org.god.ibatis.core.TransactionManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * ClassName: JDBCTransactionManager
 * PackageName: org.god.ibatis.core.transactioimpl
 * Description:
 *              JDBC事务管理器 使用原生JDBC代码管理事务
 * @Author CuiBo
 * @Create 2023/8/27 12:18
 * @Version 1.0
 */
public class JDBCTransactionManager implements TransactionManager {
    /**
     * 数据源属性
     */
    private DataSource dataSource;
    /**
     * 数据库连接属性
     */
    private Connection connection;
    /**
     * 自动提交属性：true表示自动提交事务
     *            false表示手动提交
     * 默认false
     */
    private boolean autoCommit;



    public JDBCTransactionManager() {
    }

    public JDBCTransactionManager(DataSource dataSource, boolean autoCommit) {
        this.dataSource = dataSource;
        try {
            connection=dataSource.getConnection();
            connection.setAutoCommit(autoCommit);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.autoCommit = autoCommit;
    }

    @Override
    public void rollBack() {
        try {
            connection.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void commit() {
        try {
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 数据库连接的getter方法
     * @return
     */
    @Override
    public Connection getConnection() {
        return connection;
    }


}
