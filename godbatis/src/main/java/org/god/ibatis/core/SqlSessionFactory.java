package org.god.ibatis.core;

import java.util.Map;

/**
 * ClassName: SqlSessionFactory
 * PackageName: org.god.ibatis.core
 * Description:一个数据库对应一个SqlSessionFactory，一个SqlSessionFactory对象可以开启多个会话
 *
 *  SqlSessionFactory对象，负责    sql会话的开启
 *                                          开启事务、提交事务、回滚事务、
 *                                          关闭会话
 *
 * @Author CuiBo
 * @Create 2023/8/27 10:14
 * @Version 1.0
 */
public class SqlSessionFactory {

    /**
     * 事务管理器
     */
    private TransactionManager transactionManager;


    /**
     * 存放sql映射文件标签的Map集合
     * key：sqlId（namespace+id）
     * value:MapperStatement对象
     */
    private Map<String,MapperStatement> mapperStatementMap;

    public SqlSessionFactory() {
    }

    public SqlSessionFactory(TransactionManager transactionManager, Map<String, MapperStatement> mapperStatementMap) {
        this.transactionManager = transactionManager;
        this.mapperStatementMap = mapperStatementMap;
    }

    public TransactionManager getTransactionManager() {
        return transactionManager;
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public Map<String, MapperStatement> getMapperStatementMap() {
        return mapperStatementMap;
    }

    public void setMapperStatementMap(Map<String, MapperStatement> mapperStatementMap) {
        this.mapperStatementMap = mapperStatementMap;
    }

    public SqlSession openSession(){
        SqlSession sqlSession = new SqlSession(this);
        return sqlSession;
    }

}
