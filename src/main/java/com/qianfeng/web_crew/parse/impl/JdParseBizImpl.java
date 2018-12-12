package com.qianfeng.web_crew.parse.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.qianfeng.web_crew.constants.CommonConstants;
import com.qianfeng.web_crew.domian.Page;
import com.qianfeng.web_crew.parse.IParseBiz;
import com.qianfeng.web_crew.utils.CrawlerUtils;
import com.qianfeng.web_crew.utils.HtmlUtils;
import com.qianfeng.web_crew.utils.PropertiesManagerUtil;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.Cleaner;

/**
 * Created with IDEA
 * author 光明顶斗士
 * Date:18-12-4
 * Time:下午7:23
 * Vision:1.1
 * Description:数据解析接口实现类
 */
public class JdParseBizImpl implements IParseBiz{
    private Logger logger = LoggerFactory.getLogger(JdParseBizImpl.class);

    @Override
    public void parse(Page page){
        long startTime = System.currentTimeMillis();
        String urlType = "";
        //共通的clear
        HtmlCleaner cleaner = new HtmlCleaner();
        String pageUrl = page.getUrl();
        //判断page类型
        //①单个商品页面
        //②商品列表页面
        //③品类页面
        if (pageUrl.startsWith(PropertiesManagerUtil.getPropertyValue(CommonConstants.CRAWLER_GOODS_URL_PREFIX))){
            urlType = "商品";
            parseSingleGoods(page,cleaner);
        }else  if (pageUrl.startsWith(PropertiesManagerUtil.getPropertyValue(CommonConstants.CRAWLER_GOODS_LIST_URL_PREFIX))){
            urlType = "列表";
            parseGoodesList(page,cleaner);

        }else if (pageUrl.startsWith(PropertiesManagerUtil.getPropertyValue(CommonConstants.CRAWLER_GOODS_ALL_URL_PREFIX))){
            urlType = "所有品类";
            parseAllCategories(page,cleaner);

        }//注意，根据电商平台的url，后续的开发阶段会逐步完善，如：解析 机票，图书，火车票....
        long endTime = System.currentTimeMillis();

        logger.info("页面类型：{}，url：{}，解析时间：{}ms", urlType, pageUrl, (endTime - startTime));

    }

    /**
     * 用来解析品类页面
     * @param page
     */
    private void parseAllCategories(Page page,HtmlCleaner cleaner) {
        TagNode root = cleaner.clean(page.getContent());
        try {
            //下述数组中的每个元素是大品类以及包含的所有小的品类
            Object[] objects = root.evaluateXPath("//dl[@class='clearfix']");
            if (objects != null && objects.length > 0) {
                for (Object obj : objects) {
                    TagNode perBigCategory = (TagNode) obj;

                    Object[] nowAllSonCategories = perBigCategory.evaluateXPath("//dd/a");
                    if (nowAllSonCategories != null && nowAllSonCategories.length > 0) {
                        for (Object objSon : nowAllSonCategories) {
                            TagNode sonNode = (TagNode) objSon;
                            String nowSonCatogoryUrl = "https:" + sonNode.getAttributeByName("href");
                            page.getUrls().add(nowSonCatogoryUrl);
                        }
                    }
                }
            }

        } catch (XPatherException e) {
            e.printStackTrace();
        }


    }

    /**
     * 用来解析商品列表页面在｀
     * @param page
     */
    private void parseGoodesList(Page page,HtmlCleaner cleaner) {
        TagNode root = cleaner.clean(page.getContent());
        try {
            //将当前列表页面上所有商品的url添加到容器中
            Object[] objects = root.evaluateXPath("//*[@id=\"plist\"]/ul/li[*]/div/div[1]/a");

            if (objects != null && objects.length > 0) {
                for (Object obj : objects) {
                    TagNode node = (TagNode) obj;
                    String pageUrl = node.getAttributeByName("href");
                    page.getUrls().add("https:" + pageUrl);
                }
            }

            //将当前列表页面下一页的列表页面的url也添加到容器中
            String nextPageListUrl = CrawlerUtils.getTagAttrValueByAttr(cleaner, page, "//a[@class='fp-next']", "href");
            if (nextPageListUrl != null && !nextPageListUrl.trim().isEmpty()) {
                page.getUrls().add("https://list.jd.com" + nextPageListUrl);
            }
        } catch (XPatherException e) {
            e.printStackTrace();
        }

    }

    private void parseSingleGoods(Page page,HtmlCleaner cleaner) {
        //产品的唯一标识 ~>String，UUID ，全球唯一的字符串
        page.setId(CrawlerUtils.getUUID());

        //产品编号 ~>String
        page.setGoodsId(CrawlerUtils.getGoodsId(page.getUrl()));

        //来源  ~>String （商家的网站的顶级域名，如：jd.com，taobao.com）
        page.setSource(CrawlerUtils.getTopDomian(page.getUrl()));

        //url~>String
        //页面内容 ~>String （产品url对应的页面的详细内容，别的属性都是来自于该属性）

        //标题~>String
        page.setTitle(CrawlerUtils.getTagTextValueByAttr(cleaner, page, "//div[@class='sku-name']"));


        //图片url ~> String
        String url = CrawlerUtils.getTagAttrValueByAttr(cleaner, page, "//img[@id='spec-img']", "data-origin");
        if (url == null || url.trim().isEmpty()) {
            url = CrawlerUtils.getTagAttrValueByAttr(cleaner, page, "//img[@id='spec-img']", "src");
            if (url == null || url.trim().isEmpty()) {
                url = CrawlerUtils.getTagAttrValueByAttr(cleaner, page, "//img[@id='spec-img']", "jqimg");
            }
        }
        page.setImageUrl("https:" + url);

        //售价 ~>Double
        double price = 0;
        try {
            price = CrawlerUtils.getprice("https://p.3.cn/prices/mgets?skuIds=J_" + page.getGoodsId());
        } catch (Exception e) {
            //e.printStackTrace();
        }
        page.setPrice(price);


        //评论数~>int
        //好评率 ~>Double
        HtmlUtils.getCommentCntGoodRate(page);

        //参数 ~>String
        //步骤：
        //①准备一个JSONObject的实例,和JSONArray的实例
        JSONObject obj = new JSONObject();
        JSONArray array = new JSONArray();
        obj.put("产品参数", array);

        //②解析出【商品介绍】
        JSONObject goodsIntroduce = CrawlerUtils.parseGoodInstroduction(cleaner, page);

        //③解析出【规格与包装】
        JSONObject pkg = CrawlerUtils.parseGoodsPkg(cleaner, page);


        //④将解析后的结果封装到步骤①的JSONObject的实例中
        array.add(goodsIntroduce);
        array.add(pkg);

        //⑤将最终的结果设置为Page实例的属性params的值
        page.setParams(obj.toJSONString());
    }
}
