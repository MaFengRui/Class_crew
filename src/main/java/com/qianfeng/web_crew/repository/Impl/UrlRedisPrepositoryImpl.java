package com.qianfeng.web_crew.repository.Impl;

import com.qianfeng.web_crew.constants.CommonConstants;
import com.qianfeng.web_crew.repository.IUrlPrepositoryBiz;
import com.qianfeng.web_crew.utils.JedisUtil;
import com.qianfeng.web_crew.utils.PropertiesManagerUtil;
import redis.clients.jedis.Jedis;

/**
 * Created with IDEA
 * author 光明顶斗士
 * Date:18-12-10
 * Time:下午7:33
 * Vision:1.1
 * Description:url仓库模块业务逻辑层接口实现类,分布式版爬虫使用redis内存db来存储url<br/>
 */
public class UrlRedisPrepositoryImpl implements IUrlPrepositoryBiz {
    @Override
    public void pushHigher(String url) {
        Jedis jedis = JedisUtil.getJedis();
        jedis.sadd(PropertiesManagerUtil.getPropertyValue(CommonConstants.CRAWLER_URL_REDIS_REPOSITORY_HIGHER_KEY), url);
        jedis.close();
    }

    @Override
    public void pushLower(String url) {
        Jedis jedis = JedisUtil.getJedis();
        jedis.sadd(PropertiesManagerUtil.getPropertyValue(CommonConstants.CRAWLER_URL_REDIS_REPOSITORY_LOWER_KEY), url);
        jedis.close();
    }

    @Override
    public void pushOther(String url) {
        Jedis jedis = JedisUtil.getJedis();
        jedis.sadd(PropertiesManagerUtil.getPropertyValue(CommonConstants.CRAWLER_URL_REDIS_REPOSITORY_OTHER_KEY), url);
        jedis.close();


    }

    @Override
    public String poll() {
        Jedis jedis = JedisUtil.getJedis();
        try {
            //先从高优先级的url仓库中获取一个url
            String url = jedis.spop(PropertiesManagerUtil.getPropertyValue(CommonConstants.CRAWLER_URL_REDIS_REPOSITORY_HIGHER_KEY));
            if (url == null) {
                url = jedis.spop(PropertiesManagerUtil.getPropertyValue(CommonConstants.CRAWLER_URL_REDIS_REPOSITORY_LOWER_KEY));
            }
            return url;
        }finally {
            jedis.close();
        }
    }
}
