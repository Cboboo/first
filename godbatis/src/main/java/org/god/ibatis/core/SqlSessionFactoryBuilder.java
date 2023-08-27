package org.god.ibatis.core;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.god.ibatis.core.SqlSessionFactory;
import org.god.ibatis.core.datasourceimpl.JNDIDataSource;
import org.god.ibatis.core.datasourceimpl.POOLEDDataSource;
import org.god.ibatis.core.datasourceimpl.UNPOOLEDDataSource;
import org.god.ibatis.core.transactioimpl.JDBCTransactionManager;
import org.god.ibatis.core.transactioimpl.MANAGEDTransactionManager;
import org.god.ibatis.utils.Resources;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ClassName: SqlSessionFactoryBuilder
 * PackageName: org.god.ibatis.core
 * Description: SqlSessionFactory构建器
 *
 * @Author CuiBo
 * @Create 2023/8/27 10:11
 * @Version 1.0
 */
public class SqlSessionFactoryBuilder {
    public SqlSessionFactoryBuilder() {
    }

    /**
     * 解析配置文件构建SqlSessionFactory对象并返回
     * @param is 指向核心配置文件的输入流
     * @return SqlSessionFactory对象
     */
    public SqlSessionFactory build(InputStream is){
        SqlSessionFactory sqlSessionFactory=null;
        try {
            //开始解析配置文件
            SAXReader reader = new SAXReader();
            Document configDocument = reader.read(is);
            /*
            1.首先解析configuration标签的第一个子标签environments标签
             */
            Element environments = (Element) configDocument.selectSingleNode("//environments"); //environments元素
            String defaultId = environments.attributeValue("default"); //获取environments元素default属性值
            Element environment = (Element) configDocument  //根据default属性值获取对应id值的environment元素
                    .selectSingleNode("/configuration/environments/environment[@id='" + defaultId + "']");
            Element transactionManagerEle = environment.element("transactionManager"); //获取事务管理器标签
            Element dataSourceEle = environment.element("dataSource");    //获取数据源标签
            //根据数据源标签获取数据源
            DataSource dataSource = getDataSource(dataSourceEle);
            //根据事务管理器标签获取事务管理器
            TransactionManager transactionManager = getTransactionManager(transactionManagerEle,dataSource);

            /*
            2.然后解析configuration标签的第二个子标签mappers标签
             */
            List<String> sqlMapperXMLPath = new ArrayList<>(); //存放sql映射文件路径的容器
            List<Node> mappers = configDocument.selectNodes("//mapper");//获取mappers的所有子标签mapper
            for (Node n :
                    mappers) {
                Element mapper = (Element) n;
                String resourcePath = mapper.attributeValue("resource"); //获取sql映射文件的路径
                sqlMapperXMLPath.add(resourcePath);
            }
            Map<String,MapperStatement> mapperStatementMap = getMapperStatementMap(sqlMapperXMLPath);
            sqlSessionFactory = new SqlSessionFactory(transactionManager,mapperStatementMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sqlSessionFactory;
    }

    /**
     * 解析所有的SqlMapperXML文件 构建map集合
     * @param sqlMapperXMLPath
     * @return
     */
    private Map<String, MapperStatement> getMapperStatementMap(List<String> sqlMapperXMLPath) {
        Map<String,MapperStatement> mapperStatementMap = new HashMap<>(); //map容器
        //循环解析每一个SqlMapper.xml文件
        for (String path :
                sqlMapperXMLPath) {
            try {
                //开始解析
               SAXReader reader = new SAXReader();
                Document sqlMapperDoc = reader.read(Resources.getResourceAsStream(path));
                /*
                1.首先解析根标签mapper
                 */
                Element mapperElt = (Element) sqlMapperDoc.selectSingleNode("/mapper");
                String namespace = mapperElt.attributeValue("namespace");//mapper标签的namespace属性值

                /*
                2.解析每个sql子标签
                 */
                List<Element> sqlCommandElts = mapperElt.elements(); //获取mapper标签下所有的子标签
                for (Element e :
                        sqlCommandElts) {
                    String id = e.attributeValue("id"); //循环遍历每个mapper子标签获取其id属性值
                    String sqlId = namespace+"."+id;  //namespace和id值拼接出唯一标识 - sqlId

                    String resultType = e.attributeValue("resultType");//获取select标签的resultType(结果集类型)属性值
                    String sql = e.getTextTrim();//获取标签内部的sql语句文本 去掉两边空白
                    mapperStatementMap.put(sqlId,new MapperStatement(sql,resultType));
                }
            } catch (Exception e) {
               e.printStackTrace();
            }
        }
        return mapperStatementMap;
    }

    /**
     * 获取事务管理器对象
     * @param transactionManagerEle 事务管理器标签
     * @param dataSource 数据源
     * @return
     */
    private TransactionManager getTransactionManager(Element transactionManagerEle, DataSource dataSource) {
        //获取事务管理器标签的type属性值,去掉两边空白并转大写
        String transactionManagerType = transactionManagerEle.attributeValue("type").trim().toUpperCase();
        //根据type属性值的不同 new出不同的事务管理器实现类
        if(Constant.JDBC_TRANSACTIONMANAGER.equals(transactionManagerType)){
            return new JDBCTransactionManager(dataSource,false); //默认开启事务
        }
        if(Constant.MANAGED_TRANSACTIONMANAGER.equals(transactionManagerType)){
            return new MANAGEDTransactionManager();
        }
        return null;
    }

    /**
     * 获取数据源对象
     * @param dataSourceEle 数据源标签
     * @return
     */
    private DataSource getDataSource(Element dataSourceEle) {
        Map<String,String> dataSourceConfig = new HashMap<>(); //存放数据库配置信息的容器
        List<Element> properties = dataSourceEle.elements("property"); //获取数据源标签的所有property标签
        for (Element e :
                properties) {
            String name = e.attributeValue("name");  //循环遍历取出每个property标签的name属性值
            String value = e.attributeValue("value"); //循环遍历取出每个property标签的value属性值
            dataSourceConfig.put(name,value);     //将name和value属性值放入map容器
        }
        //获取数据源标签的type属性值,去掉两边空白并转大写
        String dataSourceType = dataSourceEle.attributeValue("type").trim().toUpperCase();
        //根据type属性值的不同 new出不同的数据源实现类
        if(Constant.UN_POOLED_DATASOURCE.equals(dataSourceType)){
            return new UNPOOLEDDataSource(dataSourceConfig.get("driver"),dataSourceConfig.get("url"),dataSourceConfig.get("username"),dataSourceConfig.get("password"));
        }
        if(Constant.POOLED_DATASOURCE.equals(dataSourceType)){
            return new POOLEDDataSource();
        }
        if(Constant.JNDI_DATASOURCE.equals(dataSourceType)){
            return new JNDIDataSource();
        }
        return null;
    }
}
