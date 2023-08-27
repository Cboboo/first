package org.god.ibatis.utils;

import java.io.InputStream;

/**
 * ClassName: Resources
 * PackageName: org.god.ibatis.utils
 * Description: godbatis框架的资源工具类
 *
 * @Author CuiBo
 * @Create 2023/8/27 10:12
 * @Version 1.0
 */
public class Resources {

    private Resources(){
    }

    /**
     * 从类的根路径下加载框架的核心配置文件
     * @param resource godbatis-config.xml 核心配置文件名
     * @return 返回一个指向配置文件的输入流
     */
    public static InputStream getResourceAsStream(String resource){
        return ClassLoader.getSystemClassLoader().getResourceAsStream(resource);
    }
}
