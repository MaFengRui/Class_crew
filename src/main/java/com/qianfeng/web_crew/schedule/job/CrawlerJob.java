package com.qianfeng.web_crew.schedule.job;

import com.qianfeng.web_crew.Crawler;
import com.qianfeng.web_crew.constants.CommonConstants;
import com.qianfeng.web_crew.repository.IUrlPrepositoryBiz;
import com.qianfeng.web_crew.utils.CrawlerUtils;
import com.qianfeng.web_crew.utils.InstanceFactory;
import com.qianfeng.web_crew.utils.PropertiesManagerUtil;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Set;

/**
 * Created with IDEA
 * author 光明顶斗士
 * Date:18-12-12
 * Time:上午10:44
 * Vision:1.1
 * Description：爬虫的定时器的具体任务
 */
public class CrawlerJob implements  Job {
    /**
     * url仓库
     */
    private IUrlPrepositoryBiz urlPrepository;

    public CrawlerJob() {
        this.urlPrepository = InstanceFactory.getInstance(CommonConstants.IURLPREPOSITORYBIZ);
    }

    /**
     * 定时器倒了指定的点，下面的方法就会自动触发执行
     * @param jobExecutionContext
     * @throws JobExecutionException
     */
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        //前提
        //只要定时器启动了，证明所有的爬虫将上一次的任务都已经处理完毕，正在空转，需要清理
        //redis中key，common-url,否则就不能爬去相同的商品了（否则即使价格不桶也不能爬去）
        CrawlerUtils.cleanCommonurl(PropertiesManagerUtil.getPropertyValue(CommonConstants.CRAWLER_URL_REDIS_REPOSITORY_COMMON_KEY));
        //步骤:
        //①获得运维人员新增的种子url
        Set<String> newAddSeedUrls = CrawlerUtils.getAdminNewAddSeedUrls();
        //②将所有的种子添加到url仓库中去
        for(String url:newAddSeedUrls){
            urlPrepository.pushHigher(url);
        }
        //需要清空本次运维人员添加的新的url,(否则爬虫会处理类似的url)
        CrawlerUtils.cleanCommonurl(PropertiesManagerUtil.getPropertyValue(CommonConstants.CRAWLER_ADMIN_NEW_ADD_SEED_KEY));
    }
}
