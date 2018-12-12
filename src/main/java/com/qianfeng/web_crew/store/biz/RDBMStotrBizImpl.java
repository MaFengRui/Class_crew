package com.qianfeng.web_crew.store.biz;

import com.qianfeng.web_crew.dao.impl.DefaultPageDaoImpl;
import com.qianfeng.web_crew.domian.Page;
import com.qianfeng.web_crew.store.IStorebiz;

/**
 * Created with IDEA
 * author 光明顶斗士
 * Date:18-12-4
 * Time:下午7:17
 * Vision:1.1
 * Description:存储接口实现类
 */
public class RDBMStotrBizImpl implements IStorebiz {

    @Override
    public  void Store(Page page) {
         DefaultPageDaoImpl pageDao =new DefaultPageDaoImpl();
         pageDao.save(page);

    }
}
