package com.qianfeng.web_crew.repository.Impl;

import com.qianfeng.web_crew.repository.IUrlPrepositoryBiz;
import com.qianfeng.web_crew.utils.CrawlerUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created with IDEA
 * author 光明顶斗士
 * Date:18-12-10
 * Time:下午9:40
 * Vision:1.1
 * Description:全网单机版演示
 */
public class RandomPrepositoryAllBizImpl  implements IUrlPrepositoryBiz{
    private Map<String, Map<String, ConcurrentLinkedQueue<String>>> repository;

    public RandomPrepositoryAllBizImpl() {
        this.repository = new LinkedHashMap<>();
        random = new Random();
    }

    private Random random;

    @Override
    public void pushHigher(String url) {
        commonPush(url,"higher");
    }

    @Override
    public void pushLower(String url) {
        commonPush(url,"lower");
    }

    private void commonPush(String url, String key) {
        //步骤
        //①获得当前域名的顶级域名
        String topDomian = CrawlerUtils.getTopDomian(url);
        //②获得了当前顶级域名对应的值，类型是map<String,ConcurrentLinkQuene<String>>;存储了一个电商平台的所有结果
        Map<String, ConcurrentLinkedQueue<String>> nowPlatformAllurls = repository.getOrDefault(topDomian, new LinkedHashMap<>());
        //③需要建立当前的电商平台的
        repository.put(topDomian,nowPlatformAllurls);
        //从 Map<String, ConcurrentLinkedQueue<String>　获取高优先级的对应的值，类型是ConcurrentLinkedQueue
        ConcurrentLinkedQueue<String> higherContainer = nowPlatformAllurls.getOrDefault(key, new ConcurrentLinkedQueue<>());
        nowPlatformAllurls.put(key,higherContainer);
        //从当前的url存入的ConcurrentLinkedQueue<String>中即可
        higherContainer.add(url);
    }

    @Override
    public void pushOther(String url) {
        commonPush(url,"other");
    }

    @Override
    public String poll() {
        //随机获取一个电商平台，然后从该电商平台中获取一个url，进行后续的处理
        //步骤
        //①随机获取一个key
        Set<String> allToDomain = repository.keySet();
        String[] topDomainArr = allToDomain.toArray(new String[allToDomain.size()]);
        String randomTopMain = topDomainArr[random.nextInt(topDomainArr.length)];
        //②获得当亲随机的电商平台的所有的url
        Map<String, ConcurrentLinkedQueue<String>> nowPlatForm = repository.get(randomTopMain);
        //③获得高优先级对应的容器
        String url = null;
        ConcurrentLinkedQueue<String> higher = nowPlatForm.get("higher");
        if (higher != null){
             url = higher.poll();
        }
        if (url == null){
            ConcurrentLinkedQueue<String> lower = nowPlatForm.get("lower");
            if (lower != null)
                 url = lower.poll();
        }


        return url;
    }
}
