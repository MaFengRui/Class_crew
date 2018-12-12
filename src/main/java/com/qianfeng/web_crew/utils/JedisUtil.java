package com.qianfeng.web_crew.utils;

import com.qianfeng.web_crew.constants.CommonConstants;
import com.qianfeng.web_crew.constants.DeployMode;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * Created with IDEA
 * author 光明顶斗士
 * Date:18-12-10
 * Time:下午7:35
 * Vision:1.1
 * Description:Jedis工具类
 */
public class JedisUtil {
    private static JedisPool jedisPool;

    private JedisUtil() {
    }

    private static Properties prop;

    static {
        prop = new Properties();

        try {

            DeployMode deployMode = PropertiesManagerUtil.mode;
            String parent = deployMode.toString().toLowerCase();
            String filepath = parent + File.separator + "redis.properties";
            prop.load(JedisUtil.class.getClassLoader().getResourceAsStream(filepath));
            //将prop实例封装的jedis的参数都取出来，都天剑到共通的资源文件中去
            PropertiesManagerUtil.loadOtherProperties(prop);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取jedis实例
     *
     * @return
     */
    public static Jedis getJedis() {
        if (jedisPool == null) {
            synchronized (JedisUtil.class) {
                if (jedisPool == null) {
                    JedisPoolConfig poolConfig = new JedisPoolConfig();
                    poolConfig.setMaxIdle(Integer.valueOf(PropertiesManagerUtil.getPropertyValue(CommonConstants.CRAWLER_REDIS_MAX_IDLE)));
                    poolConfig.setMaxTotal(Integer.valueOf(PropertiesManagerUtil.getPropertyValue(CommonConstants.CRAWLER_REDIS_MAX_TOTAL)));
                    poolConfig.setMaxWaitMillis(Integer.valueOf(PropertiesManagerUtil.getPropertyValue(CommonConstants.CRAWLER_REDIS_MAX_WAIT_MILLIS)));
                    jedisPool = new JedisPool(poolConfig, PropertiesManagerUtil.getPropertyValue(CommonConstants.CRAWLER_REDIS_HOST),
                            Integer.valueOf(PropertiesManagerUtil.getPropertyValue(CommonConstants.CRAWLER_REDIS_PORT)),
                            Integer.valueOf(PropertiesManagerUtil.getPropertyValue(CommonConstants.CRAWLER_REDIS_TIMEOUT)));
                }

            }

        }
            return jedisPool.getResource();
    }

    /**
     * 关闭jedis
     * @param jedis
     */
    public static  void  close(Jedis jedis){

        if (jedis != null){
            jedis.close();
        }
    }
}
