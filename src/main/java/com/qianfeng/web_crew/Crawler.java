package com.qianfeng.web_crew;

import com.qianfeng.web_crew.constants.CommonConstants;
import com.qianfeng.web_crew.domian.Page;
import com.qianfeng.web_crew.download.IDownloadBiz;

import com.qianfeng.web_crew.parse.IParseBiz;
import com.qianfeng.web_crew.repository.IUrlPrepositoryBiz;
import com.qianfeng.web_crew.store.IStorebiz;
import com.qianfeng.web_crew.utils.CrawlerUtils;
import com.qianfeng.web_crew.utils.InstanceFactory;
import com.qianfeng.web_crew.utils.PropertiesManagerUtil;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.concurrent.Executors.*;

/**
 * Created with IDEA
 * author 光明顶斗士
 * Date:18-12-4
 * Time:下午2:21
 * Vision:1.1
 * Description:爬虫项目的入口类
 */
public class Crawler {
    private Logger logger = LoggerFactory.getLogger(Crawler.class);
   private IStorebiz storeBiz;
   private IDownloadBiz downloadBiz;
   private IParseBiz parseBiz;
   private IUrlPrepositoryBiz urlPrepositoryBiz;

    public  Crawler(){}
    public Crawler(IDownloadBiz downloadBiz, IParseBiz parseBiz, IStorebiz storebiz) {
        this.downloadBiz = downloadBiz;
        this.parseBiz = parseBiz;
        this.storeBiz = storebiz;
        //初始化容器
        urlPrepositoryBiz = InstanceFactory.getInstance(CommonConstants.IURLPREPOSITORYBIZ);
        //添加种子
        String seedurl = PropertiesManagerUtil.getPropertyValue(CommonConstants.CROWER_SEED_URL);
        urlPrepositoryBiz.pushHigher(seedurl);

    }

    /**
     * 根据指定的url下载网页资源到内存中，并将页面信息封装到实体类中
     * @param url
     * @return
     */
    public Page dowload(String url){
        return downloadBiz.download(url);
    }

    /**
     * 对page的实体类进行分析（clear + xpath）
     * @param page
     */
    public  void parse(Page page){
        parseBiz.parse(page);
    }

    /**
     * 将页面解析后的结果存储起来
     * @param page
     */

    public void store(Page page) {

        storeBiz.Store(page);
    }
    public void start() {

        checkSelf();
        //注册到zookeeper中
        register2ZK();
        //采用单机多线程进行爬取
        ExecutorService threadPool = newFixedThreadPool(3);
        while (true){
            //从url仓库中取出一个url
            String url = urlPrepositoryBiz.poll();

            if (url != null){
                threadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        Crawing(url);

                    }
                });
            }else {
                System.out.println("暂时没有新的url可供爬取哦！稍等。。。。");
                //动态休息1~2秒钟
                CrawlerUtils.sleep(2);
            }
        }

    }


    /**
     * 将当前爬虫注册到zookeeper指定的目录/curators下
     */
    private void register2ZK() {
        String zookeeperConnectionString = "myspark:2181,myspark:2182,myspark:2183";
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.newClient(zookeeperConnectionString, retryPolicy);
        client.start();

        try {
            String ipAddr = InetAddress.getLocalHost().getHostAddress() + "-" + System.currentTimeMillis();
            String nowCrawlerZNode = "/curctor/" + ipAddr;
            client.create().withMode(CreateMode.EPHEMERAL).forPath(nowCrawlerZNode, nowCrawlerZNode.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 正在爬去的操作
     * @param url
     */

    private void Crawing(String url) {

        //判断该url是否被操作过
        if (url != null && !url.trim().isEmpty()){
            if (CrawlerUtils.judgeUrlExists(url)){
                return;
            }else {
                CrawlerUtils.saveNowUrl(url);
            }
        }
        //下载
        Page page = dowload(url);
        //解析
        parse(page);
        //判断，若当前页面是列表页面的话，就把所有的商品url添加到仓库中
        List<String> urls = page.getUrls();
        if (urls != null && urls.size() > 0 ){
            //循环判断容器中每个url类型，添加到高优先级中
            for (String urlTmp:urls){
                //若是低优先级的url就添加到低优先级中
                if (urlTmp.startsWith(PropertiesManagerUtil.getPropertyValue(CommonConstants.CRAWLER_GOODS_URL_PREFIX))){

                    urlPrepositoryBiz.pushLower(urlTmp);


                }else if (urlTmp.startsWith(PropertiesManagerUtil.getPropertyValue(CommonConstants.CRAWLER_GOODS_LIST_URL_PREFIX))){

                    urlPrepositoryBiz.pushHigher(urlTmp);

                }else {
                    urlPrepositoryBiz.pushOther(urlTmp);
                }
            }
        }
        store(page);
        //休息几秒
        CrawlerUtils.sleep(2);
    }

    /**
     * 爬虫进行自检 (就是对爬虫各个组件（或是模块）进行检测)
     */
    private  void checkSelf() {
        logger.info("===============================================↓  爬虫自检开始  ↓===============================================");

        //url仓库实现类检测
        commonCheckDealWith(urlPrepositoryBiz, "url仓库实现类实例未注入！", "url仓库尚未初始化，爬虫终止运行！", "url仓库实现类实例正常注入！。。。");

        //下载自检
        commonCheckDealWith(downloadBiz, "Download实现类实例未注入！", "Download尚未初始化，爬虫终止运行！", "Download实现类实例正常注入！。。。");

        //解析自检
        commonCheckDealWith(parseBiz, "Parser实现类实例未注入！", "Parser未初始化，爬虫终止运行！", "Parser实现类实例正常注入！。。。");

        //存储自检
        commonCheckDealWith(storeBiz, "Store实现类实例未注入！", "Store未初始化，爬虫终止运行！", "Store实现类实例正常注入！。。。");

        logger.info("===============================================↑  爬虫自检结束  ↑===============================================");
    }
    /**
     * 共通的自检处理
     *
     * @param component    组件
     * @param errorMsg     错误信息
     * @param throwableMsg 异常信息
     * @param normalMsg    正常信息
     * @param <T>
     */
    private <T> void commonCheckDealWith(T component, String errorMsg, String throwableMsg, String normalMsg) {
        if (component == null) {
            logger.error(errorMsg);
            throw new ExceptionInInitializerError(throwableMsg);
        } else {
            logger.info(normalMsg);
        }
    }
    public static void main(String[] args) {
        //清空common-url
        //若标志值为０，进行清空操作
        if(args != null && PropertiesManagerUtil.getPropertyValue(CommonConstants.CRAWLER_URL_CLEAR_FIRST_FLG).equalsIgnoreCase(args[0].trim())){
            CrawlerUtils.cleanCommonurl(CommonConstants.CRAWLER_URL_REDIS_REPOSITORY_COMMON_KEY);
        }
        //启动爬虫
        IDownloadBiz downloadBiz = InstanceFactory.getInstance(CommonConstants.IDOWLOADBIZ);
        IParseBiz parseBiz = InstanceFactory.getInstance(CommonConstants.IPARSEBIZ);
        IStorebiz storebiz = InstanceFactory.getInstance(CommonConstants.ISTOREBIZ);
        new Crawler(downloadBiz,parseBiz,storebiz).start();
    }
}
