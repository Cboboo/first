package org.god.ibatis.core;

/**
 * ClassName: MapperStatement
 * PackageName: org.god.ibatis.core
 * Description: sql标签对象
 *              封装mapper映射文件中一个sql标签 <insert></insert>
 *                                          <delete></delete>
 *                                          <update></update>
 *                                          <select></select>
 * @Author CuiBo
 * @Create 2023/8/27 12:30
 * @Version 1.0
 */
public class MapperStatement {
    private String sql; //标签内的sql语句
    private String resultType; //select标签的结果集类型属性值

    public MapperStatement() {
    }

    public MapperStatement(String sql, String resultType) {
        this.sql = sql;
        this.resultType = resultType;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    @Override
    public String toString() {
        return "MapperStatement{" +
                "sql='" + sql + '\'' +
                ", resultType='" + resultType + '\'' +
                '}';
    }
}
