package com.qianfeng.web_crew.utils;

import com.alibaba.fastjson.JSON;
import com.qianfeng.web_crew.constants.CommonConstants;
import com.qianfeng.web_crew.domian.Comment.CommentBean;
import com.qianfeng.web_crew.domian.Page;
import com.qianfeng.web_crew.domian.Comment.ProductComment;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Created with IDEA
 * author 光明顶斗士
 * Date:18-12-5
 * Time:上午10:31
 * Vision:1.1
 * Description:对Html页面操作
 */

public class HtmlUtils {
    /**
     * 更具指定的url返回constant
     * @param url
     * @return
     */
    public static String dowload(String url) {
        //使用技术httpclient
        //将httpclient比作浏览器
        DefaultHttpClient client = new DefaultHttpClient();

        //请求方式;get,Post,put,delete
        HttpUriRequest request = new HttpGet(url);
        try {
            CloseableHttpResponse repose = client.execute(request);
            if (repose.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                String content = EntityUtils.toString(repose.getEntity());

                return content;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void getCommentCntGoodRate(Page page){

        //①根据url解析获得内容
        String commenturl =  CommonConstants.CREW_GETCOMMENTCNTGOODRATE_URL+page.getGoodsId()+"&_="+CrawlerUtils.getNowTimeMillions();
        //②获得html页面
        String commentText = HtmlUtils.dowload(commenturl);
        //③解析内容
        CommentBean commentBean = null;
        commentBean = JSON.parseObject(commentText, ProductComment.class).getCommentsCount().get(0);
        page.setCommentCnt(commentBean.getCommentCount());
        page.setGoodRate(commentBean.getGoodRate());

    }



}
