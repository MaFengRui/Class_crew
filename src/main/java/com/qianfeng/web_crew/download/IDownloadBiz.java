package com.qianfeng.web_crew.download;

import com.qianfeng.web_crew.domian.Page;

/**
 * Created with IDEA
 * author 光明顶斗士
 * Date:18-12-4
 * Time:下午7:19
 * Vision:1.1
 * Description:数据下载接口
 */
public interface IDownloadBiz {

    Page download(String url);
}
