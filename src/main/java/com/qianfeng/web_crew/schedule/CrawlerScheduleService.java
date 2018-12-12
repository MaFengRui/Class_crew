package com.qianfeng.web_crew.schedule;

import com.qianfeng.web_crew.schedule.job.CrawlerJob;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

/**
 * Created with IDEA
 * author 光明顶斗士
 * Date:18-12-12
 * Time:上午10:44
 * Vision:1.1
 * Description:
 */
public class CrawlerScheduleService {
    public static void main(String[] args) {
        //Grad the Scheduler instance from the Factory
        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            //and start it off
            scheduler.start();
            String jobName = CrawlerJob.class.getSimpleName();
            String groupName = "crawlerGroup";
            //String name,String group,Class,jobClass
            JobDetail jobDetail = new JobDetail(jobName, groupName, CrawlerJob.class);
            //每天凌晨1点15分url仓库传送一批种子用作爬虫的下载
            //0 15 10 ? * *  Fire at 10:15am every day
            Trigger trigger = new CronTrigger(jobName, groupName, "0 22 18 ? * *");
            //启动定时任务
            scheduler.scheduleJob(jobDetail,trigger);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
