package com.qianfeng.web_crew.repository.Impl;

import com.qianfeng.web_crew.repository.IUrlPrepositoryBiz;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created with IDEA
 * author 光明顶斗士
 * Date:18-12-10
 * Time:下午12:36
 * Vision:1.1
 * Description:url仓库模块业务逻辑接口的实现类，单机版爬虫使用链表来存储
 *
 */
public class UrlQueuePrepositoryBizImpl implements IUrlPrepositoryBiz {
    private ConcurrentLinkedQueue<String> higherLevel;
    private  ConcurrentLinkedQueue<String> lowerLecel;
    //存放的是其他类别的品类　https://jipiao.jd.com/
    private  ConcurrentLinkedQueue<String> other;

    public UrlQueuePrepositoryBizImpl() {
        this.higherLevel = new ConcurrentLinkedQueue<>();
        this.lowerLecel = new ConcurrentLinkedQueue<>();
        this.other = new ConcurrentLinkedQueue<>();
    }

    @Override

    public void pushHigher(String url) {

        higherLevel.add(url);


    }

    @Override
    public void pushLower(String url) {
        lowerLecel.add(url);

    }

    @Override
    public void pushOther(String url) {
        other.add(url);

    }

    @Override
    public String poll() {
        //从高优先级的url仓库中获取一个url;
        String url = higherLevel.poll();
        //若不存在就从低优级的容器中取出一个url
    if (url == null){
        url = lowerLecel.poll();

        } //从other容器中去取出其他类别商品进行解析，此处省略
        return url;
    }



}
