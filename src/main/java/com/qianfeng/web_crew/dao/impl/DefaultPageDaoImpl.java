package com.qianfeng.web_crew.dao.impl;

import com.qianfeng.web_crew.Crawler;
import com.qianfeng.web_crew.dao.IPageDao;
import com.qianfeng.web_crew.domian.Page;
import com.qianfeng.web_crew.utils.DBCPUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

import java.sql.SQLException;
import java.util.List;

/**
 * Created with IDEA
 * author 光明顶斗士
 * Date:18-12-4
 * Time:下午7:26
 * Vision:1.1
 * Description:将解析后的页面数据保存到DB中数据访问层实现类
 * create database if not exists `web-crew-dev`;

 CREATE TABLE  IF NOT EXISTS tb_product_info (
 id VARCHAR(50) PRIMARY  KEY,
 goodsId VARCHAR(100) NOT NULL,
 source VARCHAR(30) ,
 url VARCHAR(200) ,
 title VARCHAR (180),
 imageUrl VARCHAR(280),
 price DOUBLE(8,2),
 commentCnt INT,
 goodRate DOUBLE(5,2),
 params Text
 );

 */
public class DefaultPageDaoImpl implements IPageDao {
    private  QueryRunner qr = new QueryRunner(DBCPUtils.getDataSource());


    /**
     * 数据存储，判断页面是否存在
     * @param page
     */
    public void save(Page page) {
        List<String> urls = page.getUrls();
        //若是列表页面，就不需要保存
        if (urls != null && urls.size() > 0) {
            return;
        }
        String sql = "select * from tb_product_info where goodsId=? and source=?";

        try {
            Page bean = qr.query(sql,new BeanHandler<>(Page.class),page.getGoodsId(),page.getSource());
            //不存在就添加
            if (bean != null) {
                updateToDB(page,bean);
                }else {
                insert(page);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * 存在一个Page的对象，更新最新信息
     * @param page
     */

    public void updateToDB(Page page,Page beanFromDB) {
        String sql = "update  tb_product_info  set title=?," +
                "imageUrl=?," +
                "price=?," +
                "commentCnt=?," +
                "goodRate=?," +
                "params=? where id=?";
        try {
            qr.update(sql,
                    page.getTitle(),
                    page.getImageUrl(),
                    page.getPrice(),
                    page.getCommentCnt(),
                    page.getGoodRate(),
                    page.getParams(),
                    beanFromDB.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 插入一个新的Page对象到数据库中
     * @param page
     */

    public  void  insert(Page page){
        String sql = "INSERT INTO tb_product_info VALUES(?,?,?,?,?,?,?,?,?,?)";
        try {
            qr.update(sql,
                    page.getId(),
                    page.getGoodsId(),
                    page.getSource(),
                    page.getUrl(),
                    page.getTitle(),
                    page.getImageUrl(),
                    page.getPrice(),
                    page.getCommentCnt(),
                    page.getGoodRate(),
                    page.getParams());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

