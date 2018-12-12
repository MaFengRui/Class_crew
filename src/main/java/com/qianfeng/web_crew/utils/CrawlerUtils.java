package com.qianfeng.web_crew.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.qianfeng.web_crew.constants.CommonConstants;
import com.qianfeng.web_crew.domian.Page;
import com.qianfeng.web_crew.domian.price.PriceBean;
import com.qianfeng.web_crew.domian.price.ProductPrice;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;
import redis.clients.jedis.Jedis;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.qianfeng.web_crew.utils.HtmlUtils.dowload;

/**
 * Created with IDEA
 * author 光明顶斗士
 * Date:18-12-4
 * Time:下午2:23
 * Vision:1.1
 * Description:爬虫项目的工具类
 * @author mafenrgui
 */
public  class CrawlerUtils {
    /**
     * 获得全球唯一的随机字符串
     */
    public static  String getUUID(){
        return UUID.randomUUID().toString().replace("-","");
    }

    /**
     * 获得产品id
     * @param url 如：https://item.jd.com/8735204.html
     * @return
     */
   public static String getGoodsid(String url){
        int beginIndex = url.lastIndexOf('/')+1;
        int endIndex = url.lastIndexOf('.');
        return url.substring(beginIndex,endIndex);

   }

    /**
     * 获取指定url的顶级域名
     * @param url
     * @return
     */
   public static String getTopDomian(String url){

       try {
           String host = new URI(url).getHost().toLowerCase(); //此处获取值转为小写
           Pattern pattern = Pattern.compile("[^\\.]+(\\.com\\.cn|\\.net\\.cn|\\.org\\.cn|\\.gov\\.cn|\\.com|\\.net|\\.cn|\\.org|\\.cc|\\.me|\\.tel|\\.mobi|\\.asia|\\.biz|\\.info|\\.name|\\.tv|\\.hk|\\.公司|\\.中国|\\.网络)");
           Matcher matcher = pattern.matcher(host);
           while (matcher.find()){
               return matcher.group();
           }
       } catch (URISyntaxException e) {
           e.printStackTrace();
       }
       return null;


   }

    /**
     * 得到商品id
     * @param url
     * @return
     */
    public static String getGoodsId(String url) {
        int beginIndex = url.lastIndexOf('/') + 1;
        int endIndex = url.lastIndexOf('.');
        return url.substring(beginIndex, endIndex);
    }


    /**
     * 根据指定的xpath，获取对应标签的标签体的值
     * @param cleaner
     * @param page
     * @param xpath
     * @return
     */
    public static String getTagTextValueByAttr(HtmlCleaner cleaner, Page page,String xpath){
        TagNode tagNode = cleaner.clean(page.getContent());

        try {
            Object[] objs = tagNode.evaluateXPath(xpath);
            if (objs != null && objs.length > 0){
                TagNode node = (TagNode) objs[0];
                String value = node.getText().toString().trim();
                return value;
            }

        } catch (XPatherException e) {
            e.printStackTrace();
        }
        return  null;
    }


    /**
     *
     * 根据指定的xpath，获取对应标签的属性值
     * @param cleaner
     * @return
     */

    public  static String getTagAttrValueByAttr(HtmlCleaner cleaner,Page page,String xpath,String arrName){
        TagNode tagNode = cleaner.clean(page.getContent());

        try {
            Object[] objects = tagNode.evaluateXPath(xpath);
            if (objects != null && objects.length > 0){
                TagNode node = (TagNode) objects[0];
                String value = node.getAttributeByName(arrName);
                return value;
            }

        } catch (XPatherException e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * 根据价格的url得到价格
     * @param priceurl
     * @return
     */

    public static Double getprice(String priceurl){
        //①根据url获得内容
        String content = dowload(priceurl);
        //②解析内容
        JSONArray jsonArray = JSON.parseArray(content);
        //＠解析json数组
        JSONObject obj = new JSONObject();
        obj.put("beans",jsonArray);
        ProductPrice productPrice = JSON.parseObject(obj.toJSONString(), ProductPrice.class);
        PriceBean bean = productPrice.getBeans().get(0);
        String p = bean.getP();

        return Double.valueOf(p);

    }

    /**
     * 获得当前时间的毫秒数
     * @return
     */
    public static long getNowTimeMillions(){
        return new Date().getTime();

    }

    /**
     * 解析商品的主要参数
     * @param cleaner
     * @param page
     * @return
     */
    public static JSONObject parseGoodInstroduction(HtmlCleaner cleaner,Page page){
        JSONObject goodsIntroduce = new JSONObject();
        String goodsIntoduction = CrawlerUtils.getTagTextValueByAttr(cleaner, page, "//li[@clstag='shangpin|keycount|product|shangpinjieshao_1']");
        JSONObject goodsIntroduceDetail = new JSONObject();
        goodsIntroduce.put(goodsIntoduction, goodsIntroduceDetail);


        TagNode tagNode = cleaner.clean(page.getContent());
        try {
            //关于商品介绍中主要参数
            Object[] objects = tagNode.evaluateXPath("//*[@id=\"detail\"]/div[2]/div[1]/div[1]/ul[1]/li[*]/div");

            if (objects != null && objects.length > 0) {
                for (Object tmpObj : objects) {
                    TagNode node = (TagNode) tmpObj;

                    for (TagNode childNode : node.getChildTags()) {
                        String body = childNode.getText().toString();
                        String[] arr = body.split("：");
                        goodsIntroduceDetail.put(arr[0].trim(), arr[1].trim());
                    }
                }
            }

            //解析商品介绍中中的品牌
            String brand = CrawlerUtils.getTagTextValueByAttr(cleaner, page, "//ul[@id='parameter-brand']");
            if (brand != null) {
                String[] arr = brand.split("：");
                goodsIntroduceDetail.put(arr[0].trim(), arr[1].trim());
            }

            //商品介绍中其他的参数
            objects = tagNode.evaluateXPath("//ul[@class='parameter2 p-parameter-list']");
            if (objects != null && objects.length > 0) {
                TagNode parentNode = (TagNode) objects[0];
                for (TagNode child : parentNode.getChildTags()) {
                    String[] arrTmp = child.getText().toString().trim().split("：");
                    goodsIntroduceDetail.put(arrTmp[0].trim(), arrTmp[1].trim());
                }
            }

            return goodsIntroduce;
        } catch (XPatherException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject parseGoodsPkg(HtmlCleaner cleaner,Page page){
        String pkginfo = CrawlerUtils.getTagTextValueByAttr(cleaner,page,"//li[@clstag='shangpin|keycount|product|pcanshutab']");
        JSONObject pkgobj = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        pkgobj.put(pkginfo,jsonArray);
        TagNode tagNode = cleaner.clean(page.getContent());
        try {
            //获得所有分类信息的标签
            Object[] objects = tagNode.evaluateXPath("//div[@class='Ptable-item']");
            if (objects != null && objects.length > 0){
                for (Object object : objects){
                    //每循环一次构建一个JsonObject对象，将结果填充到object中
                    JSONObject object1 = new JSONObject();
                    //第i个分类主体
                    TagNode tagNode1 = (TagNode) object;
                    String key = tagNode1.getChildTags()[0].getText().toString();
                    JSONObject perLine = new JSONObject();
                    //建立关联联系
                    object1.put(key,perLine);
                    //填充perLine
                    //根据当前的tagNode获得标签，子标签的特性
                    Object[] objects1 = tagNode1.evaluateXPath("//dl[@class='clearfix']");

                    for (int i = 0; i < objects1.length;i++){
                        TagNode tagNode2 = (TagNode) objects1[i];
                        TagNode[] tags = tagNode2.getChildTags();
                        //每一个详细参数的key，Value
                        List list = new LinkedList<>();
                        list.clear();
                        for (int j = 0;j<tags.length;j++){
                            TagNode tag = tags[j];
                            if (tag.getChildTags().length == 0){
                                list.add(tag.getText().toString());
                            }
                        }
                        perLine.put(list.get(0).toString(),list.get(list.size()-1));

                    }
                    jsonArray.add(perLine);
                }
            }
            return pkgobj;

        } catch (XPatherException e) {
            e.printStackTrace();
        }
        return  null;
    }


    public static void sleep(int randomSeconds) {

        int random = (int ) (Math.random()*randomSeconds +1 );
        try {
            Thread.sleep(random * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    /**
     * 判断当前url在redis共同仓库中是否存在
     * @param url
     * @return
     */
    public static boolean judgeUrlExists(String url) {
        Jedis jedis = null;
        try {
            String commonRepKey = PropertiesManagerUtil.getPropertyValue(CommonConstants.CRAWLER_URL_REDIS_REPOSITORY_COMMON_KEY);
            jedis = JedisUtil.getJedis();
            return jedis.sismember(commonRepKey,url);
        }finally {
            if (jedis != null){
                jedis.close();
            }
        }


    }

    /**
     * 保存所有爬虫尚未处理的url
     * @param url
     */
    public static void saveNowUrl(String url) {
        Jedis jedis = null;
        try {
            String commonRepKey =  PropertiesManagerUtil.getPropertyValue(CommonConstants.CRAWLER_URL_REDIS_REPOSITORY_COMMON_KEY);
            jedis = JedisUtil.getJedis();
            jedis.sadd(commonRepKey,url);
        }finally {
            if (jedis != null){
                jedis.close();
            }
        }
    }

    /**
     * 清除共通的url仓库
     */

    public static void cleanCommonurl(String key) {

        Jedis jedis = null;
        try {

            jedis = JedisUtil.getJedis();
            jedis.del(PropertiesManagerUtil.getPropertyValue(key));
        }finally {
            if (jedis != null) {
                jedis.close();
            }
        }




    }

    /**
     * 获取运维人员新加的顶级域名
     */

    public static Set<String> getAdminNewAddSeedUrls() {
        Jedis jedis = null;
        Set<String> allUrls = null;
        try {
            jedis = JedisUtil.getJedis();

            allUrls = jedis.smembers(PropertiesManagerUtil.getPropertyValue(CommonConstants.CRAWLER_ADMIN_NEW_ADD_SEED_KEY));
            return allUrls;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
}
