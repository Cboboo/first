package org.god.ibatis.core;

import java.lang.reflect.Method;
import java.sql.*;

/**
 * ClassName: SqlSession
 * PackageName: org.god.ibatis.core
 * Description: 负责执行sql语句的会话对象
 *
 * @Author CuiBo
 * @Create 2023/8/27 15:28
 * @Version 1.0
 */
public class SqlSession {
    private SqlSessionFactory sqlSessionFactory;

    public SqlSession(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    /**
     * 根据pojo类插入一条记录
     * @param sqlId 执行的sql语句的id
     * @param pojo 插入的实体类
     * @return
     */
    public int insert(String sqlId,Object pojo){
        int count = 0;
        try {
            //获取数据库连接
            Connection connection = sqlSessionFactory.getTransactionManager().getConnection();
            //获取未替换的sql语句
            String oldSql = sqlSessionFactory.getMapperStatementMap().get(sqlId).getSql().trim();
            //替换占位符
            String sql = oldSql.replaceAll("#\\{[0-9A-Za-z_$]*}", "?");
            //预编译sql语句
            PreparedStatement ps = connection.prepareStatement(sql);

            /*
            找出sql语句中有多少个位置需要传值
            每个位置需要传什么值
             */
            int fromIndex = 0; //起始索引
            int index=1;   //记录占位符索引
            while (true){
                int jingIndex = oldSql.indexOf("#",fromIndex); //'#' 符号所在位置
                int youkuohaoIndex = oldSql.indexOf("}",jingIndex); // '}' 符号所在位置
                if(-1==jingIndex){ //等于-1 说明不存在占位符，跳出循环
                    break;
                }
                String propertyName = oldSql.substring(jingIndex + 2, youkuohaoIndex).trim(); //截取属性名
                fromIndex=youkuohaoIndex+1; //调整起始索引，准备下一轮查找

                //拼接得到占位符位置属性的getter方法
                String getMethodName = "get" + propertyName.toUpperCase().charAt(0) + propertyName.substring(1);
                //通过反射执行getter方法
                Class<?> clazz = pojo.getClass();
                Method getMethod = clazz.getDeclaredMethod(getMethodName);
                //返回对应属性的值用以填充
                Object propertyValue = getMethod.invoke(pojo);
                //填充占位符
                ps.setObject(index++,propertyValue);
            }
            //执行sql语句
            count= ps.executeUpdate();
        } catch (Exception e) {
           e.printStackTrace();
        }
        return count;
    }

    /**
     * 根据给定参数查询表中记录
     * @param sqlId 执行的sql语句的id
     * @param param 参数
     * @return
     */
    public Object selectOne(String sqlId,Object param){
        Object result = null;
        try {
            //获取连接
            Connection connection = sqlSessionFactory.getTransactionManager().getConnection();
            //获取未替换的sql语句
            String oldSql = sqlSessionFactory.getMapperStatementMap().get(sqlId).getSql().trim();
            //替换占位符
            String sql = oldSql.replaceAll("#\\{[0-9A-Za-z_$]*}", "?");
            //预编译sql语句
            PreparedStatement ps = connection.prepareStatement(sql);
            //填充占位符
            ps.setObject(1,param);
            //执行sql获得结果集对象
            ResultSet resultSet = ps.executeQuery();
            //获取结果集的元数据
            ResultSetMetaData metaData = resultSet.getMetaData();
            //根据元数据获取列数
            int columnCount = metaData.getColumnCount();
            //获得select语句要映射的pojo类类型
            String resultType = sqlSessionFactory.getMapperStatementMap().get(sqlId).getResultType();
            if(resultSet.next()){
                //加载pojo类
                Class<?> clazz = Class.forName(resultType);
                //创建pojo类对象
                result = clazz.getDeclaredConstructor().newInstance();
                for (int i = 0; i < columnCount; i++) {
                    String columnLabel = metaData.getColumnLabel(i + 1); //元数据中获取列的别名
                    Object propertyValue = resultSet.getObject(columnLabel); //获取对应列的值
                    //拼接得到setter方法
                    String setMethodName = "set"+columnLabel.toUpperCase().charAt(0)+columnLabel.substring(1);
                    //反射获取setter方法
                    Method setMethod = clazz.getDeclaredMethod(setMethodName, propertyValue.getClass());
                    //执行setter方法，将查询到的数据封装到对应pojo类
                    setMethod.invoke(result,propertyValue);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 事务提交
     */
    public void commit(){
        sqlSessionFactory.getTransactionManager().commit();
    }

    /**
     * 事务回滚
     */
    public void rollBack(){
        sqlSessionFactory.getTransactionManager().rollBack();
    }

    /**
     * 会话关闭
     */
    public void close(){
        sqlSessionFactory.getTransactionManager().close();
    }
}
