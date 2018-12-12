package com.qianfeng.web_crew.repository;

/**
 * Created with IDEA
 * author 光明顶斗士
 * Date:18-12-10
 * Time:下午12:30
 * Vision:1.1
 * Description:url仓库模块业务逻辑层接口
 */
public interface IUrlPrepositoryBiz {
    /**
     * 向高优先级的容器中存放url，存放商品列表页面的url
     *
     */
    void  pushHigher(String url);
    /**
     * 向低优先级的容器中存放url,存放商品详情页面的url
     */
    void pushLower(String url);

    /**
     * 将其他的url存放到仓库中，后续完善阶段将other的url在进行细分
     */
    void pushOther(String url);
    /**
     * 从容器中获取url
     */
    String poll();
}
