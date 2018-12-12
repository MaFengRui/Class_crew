package com.qianfeng.web_crew.store.biz;

import com.qianfeng.web_crew.domian.Page;
import com.qianfeng.web_crew.store.IStorebiz;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IDEA
 * author 光明顶斗士
 * Date:18-12-10
 * Time:下午1:40
 * Vision:1.1
 * Description:在控制台上显示结果
 */
public class ConsoleShowBizImpl implements IStorebiz {
    private Logger logger = LoggerFactory.getLogger(ConsoleShowBizImpl.class);
    @Override
    public void Store(Page page) {
        logger.info("线程名：{}，url：{}，售价(注：列表页面为0.0)：{}", Thread.currentThread().getName(), page.getUrl(), page.getPrice());
    }
}
