package org.god.ibatis.core.transactioimpl;

import org.god.ibatis.core.TransactionManager;

import java.sql.Connection;

/**
 * ClassName: MANAGEDTransactionManager
 * PackageName: org.god.ibatis.core.transactioimpl
 * Description:
 *              MANAGED事务管理器 使用第三方框架管理
 * @Author CuiBo
 * @Create 2023/8/27 12:20
 * @Version 1.0
 */
public class MANAGEDTransactionManager implements TransactionManager {
    @Override
    public void rollBack() {

    }

    @Override
    public void commit() {

    }

    @Override
    public void close() {

    }

    @Override
    public Connection getConnection() {
        return null;
    }
}
