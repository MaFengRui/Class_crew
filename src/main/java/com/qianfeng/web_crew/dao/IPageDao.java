package com.qianfeng.web_crew.dao;

import com.qianfeng.web_crew.domian.Page;

/**
 * Created with IDEA
 * author 光明顶斗士
 * Date:18-12-4
 * Time:下午2:18
 * Vision:1.1
 * Description:将解析后的页面数据保存到DB中数据访问层接口
 */
public interface IPageDao {
     void save(Page page);

}
