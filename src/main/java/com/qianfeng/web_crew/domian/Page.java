package com.qianfeng.web_crew.domian;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;

/**
 * Created with IDEA
 * author 光明顶斗士
 * Date:18-12-4
 * Time:下午4:21
 * Vision:1.1
 * Description:
 */
@Data
public class Page {
    /**
     * 产品的唯一标识 ~>String，UUID ，全球唯一的字符串
     */
    private String id;

    /**
     * 产品编号 ~>String
     */
    private String goodsId;

    /**
     * 来源  ~>String （商家的网站的顶级域名，如：jd.com，taobao.com）
     */
    private String source;


    /**
     * url~>String
     */
    private String url;

    /**
     * 页面内容 ~>String （产品url对应的页面的详细内容，别的属性都是来自于该属性）
     *
     * 注意：建表时，该属性不需要映射为相应的字段。为了辅助计算其他的属性值。
     */
    private String content;

    /**
     * 标题~>String
     */
    private String title;

    /**
     * 图片url ~>String
     */
    private String imageUrl;


    /**
     * 售价 ~>Double
     */
    private double price;

    /**
     * 评论数~>int
     */
    private int commentCnt;

    /**
     * 好评率 ~>Double
     */
    private double goodRate;

    /**
     * 参数 ~>String, 向表中相应的字段处存入一个json对象格式的数据
     */
    private String params;

    /**
     * 一个类中，若类的属性有集合类型，一般需要进行手动初始化
     *
     * @return
     */
    private List<String> urls= new LinkedList<>();

    @Override
    public String toString() {
        return "Page{" +
                "id='" + id + '\'' +
                ", goodsId='" + goodsId + '\'' +
                ", source='" + source + '\'' +
                ", url='" + url + '\'' +
                ", title='" + title + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", price=" + price +
                ", commentCnt=" + commentCnt +
                ", goodRate=" + goodRate +
                ", params='" + params + '\'' +
                '}';
    }
}
