package com.qianfeng.web_crew.curator;


import com.qianfeng.web_crew.utils.MailUtil;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created with IDEA
 * author 光明顶斗士
 * Date:18-12-11
 * Time:下午8:41
 * Vision:1.1
 * Description:爬虫服务监控类
 */
public class CuratorService implements Watcher{
    private Logger logger = LoggerFactory.getLogger(CuratorService.class);
    private CuratorFramework client;
    /**
     * 存放所有子孩子的名字
     */
    private List<String> initAllZnodes;

    public CuratorService() {
        String zookeeperConnectionString = "myspark:2181,myspark:2182,myspark:2183";

        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.newClient(zookeeperConnectionString, retryPolicy);
        //注意：在start方法之后书写具体的操作
        client.start();

        try {
            initAllZnodes = client.getChildren().usingWatcher(this).forPath("/curctor");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 监控父节点下多有的子节点
     * @param watchedEvent
     */

    @Override
    public void process(WatchedEvent watchedEvent) {
        List<String> newZodeInfos = null;
        try {
            newZodeInfos = client.getChildren().usingWatcher(this).forPath("/curctor");

            //概述：根据初始话容器的长度与最新的容器的长度进行比对，就可以推导出当前爬虫集群的状态：新增，宕机，变更...

            //思想：哪个容器中元素多，就循环遍历哪个容器。

            //新增
            if (newZodeInfos.size() > initAllZnodes.size()) {
                //明确显示新增了哪个爬虫节点
                for (String nowZNode : newZodeInfos) {
                    if (!initAllZnodes.contains(nowZNode)) {
                        // System.out.printf("新增爬虫节点【%s】%n", nowZNode);
                        logger.info("新增爬虫节点{}", nowZNode);
                    }
                }
            } else if (newZodeInfos.size() < initAllZnodes.size()) {
                //宕机
                //明确显示哪个爬虫节点宕机了
                for (String initZNode : initAllZnodes) {
                    if (!newZodeInfos.contains(initZNode)) {
//                        System.out.printf("爬虫节点【%s】宕机了哦！要赶紧向运维人员发送E-mail啊！....%n", initZNode);
                        logger.info("爬虫节点【{}】宕机了哦！要赶紧向运维人员发送E-mail啊！....", initZNode);
                        //发邮件 （发短信，发微信，qq....）
                        //String 	subject, String content, String recipientPersonEmail, String recipientPersonName
                        MailUtil.sendWarningEmail("爬虫宕机警告！", "爬虫节点【" + initZNode + "】宕机了哦！请您赶紧采取应对措施！...", "mfr8520@163.com", "test");

                        //分布式爬虫的HA
//                       Process ps = Runtime.getRuntime().exec("/opt/crawler/crawler.sh");
//                        ps.waitFor();
//                        BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
//                        String line = null;
//                        while ((line = br.readLine()) != null) {
//                            logger.info(line);
//                        }
                    }
                }
            } else {
                //容器中爬虫的个数未发生变化（不用处理）
                //①爬虫集群正常运行
                //②宕机了，当时马上重启了，总的爬虫未发生变化
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


        //要达到每次都是与上一次比较的效果，需要动态替换：initAllZnodes
        initAllZnodes = newZodeInfos;


    }
    private void start() {
        while (true) {
        }
    }
    public static void main(String[] args) {
        //监控服务启动
        new CuratorService().start();
    }
}


