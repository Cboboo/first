package org.god.ibatis.core;

import java.sql.Connection;

/**
 * ClassName: TransactionManager
 * PackageName: org.god.ibatis.core
 * Description:
 *              事务管理器 负责事务的开启、提交、关闭
 *
 * @Author CuiBo
 * @Create 2023/8/27 12:14
 * @Version 1.0
 */
public interface TransactionManager {
    /**
     * 事务回滚
     */
    void rollBack();

    /**
     * 事务提交
     */
    void commit();

    /**
     * 事务关闭
     */
    void close();

    Connection getConnection();
}
