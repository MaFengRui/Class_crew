package com.qianfeng.web_crew.download.impl;

import com.qianfeng.web_crew.domian.Page;
import com.qianfeng.web_crew.download.IDownloadBiz;
import com.qianfeng.web_crew.utils.HtmlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IDEA
 * author 光明顶斗士
 * Date:18-12-4
 * Time:下午7:20
 * Vision:1.1
 * Description:数据下载接口实现类
 */
public class DownLoadBizImpl implements IDownloadBiz {
    private Logger logger = LoggerFactory.getLogger(DownLoadBizImpl.class);
    @Override
    public Page download(String url){
        long startTime = System.currentTimeMillis();
        Page page = new Page();
        //使用技术httpclient
        //将httpclient比作浏览器
        //请求方式;get,Post,put,delete
        String context = new HtmlUtils().dowload(url);
        page.setContent(context);
        page.setUrl(url);
        long endTime = System.currentTimeMillis();
        logger.info("下载url：{}，耗费了{}ms！", url, (endTime - startTime));
        return page;
    }

}
