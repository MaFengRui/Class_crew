package wev_crew;

import com.qianfeng.web_crew.Crawler;
import com.qianfeng.web_crew.domian.Page;
import org.junit.Test;

/**
 * Created with IDEA
 * author 光明顶斗士
 * Date:18-12-4
 * Time:下午9:03
 * Vision:1.1
 * Description:
 */
public class CrawlerTest {
    @Test
    public void  crawlerbeginDoingTest(){
        //步骤
        //第一步准备爬虫实例
        Crawler crawler = new Crawler();
        //调用爬虫实例的方法
        String url = "https://item.jd.com/5089275.html";
        Page page = crawler.dowload(url);
//        https://p.3.cn/prices/mgets?skuIds=J_5089275
        //(1)下载
        crawler.dowload(url);
        //（２）解析
        crawler.parse(page);
        System.out.println(page);
//        (3)存储
        crawler.store(page);
    }
}
