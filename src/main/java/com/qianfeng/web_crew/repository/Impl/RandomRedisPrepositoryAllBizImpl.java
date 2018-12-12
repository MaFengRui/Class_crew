package com.qianfeng.web_crew.repository.Impl;

import com.qianfeng.web_crew.constants.CommonConstants;
import com.qianfeng.web_crew.repository.IUrlPrepositoryBiz;
import com.qianfeng.web_crew.utils.CrawlerUtils;
import com.qianfeng.web_crew.utils.JedisUtil;
import com.qianfeng.web_crew.utils.PropertiesManagerUtil;
import redis.clients.jedis.Jedis;

import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

/**
 * Created with IDEA
 * author 光明顶斗士
 * Date:18-12-10
 * Time:下午11:43
 * Vision:1.1
 * Description:全网爬虫分布式版
 */
public class RandomRedisPrepositoryAllBizImpl implements IUrlPrepositoryBiz {
    private Random random;
    private Set<String> allTopDomains;

    public RandomRedisPrepositoryAllBizImpl() {
        random = new Random();
        allTopDomains = new LinkedHashSet<>();
    }

    @Override
    public void pushHigher(String url) {
        commonPush(url, PropertiesManagerUtil.getPropertyValue(CommonConstants.CRAWLER_URL_REDIS_REPOSITORY_HIGHER_KEY));

    }

    @Override
    public void pushLower(String url) {
        commonPush(url, PropertiesManagerUtil.getPropertyValue(CommonConstants.CRAWLER_URL_REDIS_REPOSITORY_LOWER_KEY));
    }

    @Override
    public void pushOther(String url) {
        commonPush(url, PropertiesManagerUtil.getPropertyValue(CommonConstants.CRAWLER_URL_REDIS_REPOSITORY_OTHER_KEY));


    }

    private void commonPush(String url, String nowUrlLevel) {
        //思路：
        //①获得顶级域名
        String topDomain = CrawlerUtils.getTopDomian(url);

        //将当前处理的dopDomain加到容器中
        allTopDomains.add(topDomain);

        //②获得Jedis的实例
        Jedis jedis = JedisUtil.getJedis();

        //③组织key,如：电商平台的顶级域名.higer-level
        String key = topDomain + "." + nowUrlLevel;

        //④存入
        jedis.sadd(key, url);

        //⑤资源释放
        jedis.close();
    }

    @Override
    public String poll() {
        //前提：
        Jedis jedis = JedisUtil.getJedis();
        //①从共通的url中取出所有的不同的顶级域名，存储到Set容器中
//        Set<String> allTopDomains = CrawlerUtils.getAllTopDomains();
//        //拦截非法的操作
//        if (allTopDomains == null || allTopDomains.size() == 0) {
//            //证明是第一次启动,单独存储种子url
//            allTopDsomains.add(PropertiesManagerUtil.getPropertyValue(CommonConstants.CRAWLER_SEED_URL));
//        }
        //②随机获取一个顶级域名
        String[] topDomainArr = allTopDomains.toArray(new String[allTopDomains.size()]);
        int index = random.nextInt(topDomainArr.length);
        String randomTopDomain = topDomainArr[index];
        //③先从高优先级的key中获取url
        String key = randomTopDomain + "." + PropertiesManagerUtil.getPropertyValue(CommonConstants.CRAWLER_URL_REDIS_REPOSITORY_HIGHER_KEY);
        String url = jedis.spop(key);
        //④若高优先级的key取完了，从低优先级的key中获取（other:本期暂时不考虑）
        if (url == null) {
            key = randomTopDomain + "." + PropertiesManagerUtil.getPropertyValue(CommonConstants.CRAWLER_URL_REDIS_REPOSITORY_LOWER_KEY);
            url = jedis.spop(key);
        }
        //释放
        jedis.close();

        return url;
        
    }
}
