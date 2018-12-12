package com.qianfeng.web_crew.domian.price;

import lombok.Data;

import java.util.List;

/**
 * Created with IDEA
 * author 光明顶斗士
 * Date:18-12-5
 * Time:上午10:27
 * Vision:1.1
 * Description:产品售价实体类
 */
@Data
public class ProductPrice{
    //获得是json数据
    //[{"op":"1399.00","m":"9999.00","id":"J_7479820","p":"1199.00"}]
    private List<PriceBean> beans;

}


