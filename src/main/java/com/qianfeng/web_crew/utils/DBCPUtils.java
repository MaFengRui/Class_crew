package com.qianfeng.web_crew.utils;

import com.qianfeng.web_crew.constants.CommonConstants;
import com.qianfeng.web_crew.constants.DeployMode;
import org.apache.commons.dbcp.BasicDataSourceFactory;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created with IDEA
 * author 光明顶斗士
 * Date:18-12-4
 * Time:下午2:04
 * Vision:1.1
 * Description:对DBCP连接池操作的工具类
 */
public class DBCPUtils {
    /**
     * 数据源类型的属性（就是连接池）
     */

    private static DataSource ds;
    /**
     * 操作数据源文件的map集合
     * 通过他动态获取连接模式（），创建连接池
     */
    private static Properties properties;

    static {
        properties = new Properties();
        //获得模式名（通过共通的资源文件管理器工具类来获取）
        DeployMode deployMode = PropertiesManagerUtil.mode;
        String resourceName = deployMode.toString().toLowerCase() + File.separator+ CommonConstants.DBCP_COMMON_FILE_NAME;
        try {
            properties.load(DBCPUtils.class.getClassLoader().getResourceAsStream(resourceName));
            //初始化连接池
            ds = BasicDataSourceFactory.createDataSource(properties);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    public static  DataSource getDataSource(){
        return ds;
    }
    public static Connection getConnection(){
        try {
        return ds.getConnection();
    } catch (SQLException e) {
        e.printStackTrace();
        throw new RuntimeException(PropertiesManagerUtil.getPropertyValue(CommonConstants.CONNNECTION_FALURE_MSG));
    }

    }

}
