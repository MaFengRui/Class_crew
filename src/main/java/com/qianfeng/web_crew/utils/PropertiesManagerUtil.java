package com.qianfeng.web_crew.utils;

import com.qianfeng.web_crew.constants.CommonConstants;
import com.qianfeng.web_crew.constants.DeployMode;

import java.io.IOException;
import java.util.Properties;

/**
 * Created with IDEA
 * author 光明顶斗士
 * Date:18-12-4
 * Time:下午2:05
 * Vision:1.1
 * Description:资源文件操作工具类
 */
public class PropertiesManagerUtil {
    /**
     * 资源文本操作的容器
     */
    private static Properties properties;

    /**
     * 选择部署模式
     */
    public static DeployMode mode;
    static {

        properties = new Properties();

            try {
                properties.load(PropertiesManagerUtil.class.getClassLoader().getResourceAsStream(CommonConstants.COMMON_CONFIG_FILE_NAME));
            } catch (IOException e) {
                e.printStackTrace();
            }


        mode = DeployMode.valueOf(getPropertyValue(CommonConstants.CRAWLER_JOB_RUN_MODE).toUpperCase());
    }

    public static  String getPropertyValue(String Key){
        return properties.getProperty(Key);
    }

    /**
     * 将其他资源文件中的数据合并到当前properties实例中
     * @param prop
     */
    public static void loadOtherProperties(Properties prop) {
        properties.putAll(prop);

    }
}

