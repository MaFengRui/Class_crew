package com.qianfeng.web_crew.store.biz;

import com.qianfeng.web_crew.domian.Page;
import com.qianfeng.web_crew.store.IStorebiz;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * Created with IDEA
 * author 光明顶斗士
 * Date:18-12-7
 * Time:下午12:52
 * Vision:1.1
 * Description:将数据保存在hbase
 */
public class HbaseStotrBizImpl implements IStorebiz {

    Logger logger = LoggerFactory.getLogger(HbaseStotrBizImpl.class);
    private Table table;

    public HbaseStotrBizImpl() {
        try {
            Connection connection = ConnectionFactory.createConnection();
            this.table =connection.getTable(TableName.valueOf("tb_product_info"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void Store(Page page) {
        List<String> urls = page.getUrls();
        //若是列表页面，就不需要保存
        if (urls != null && urls.size() > 0) {
            return;
        }
        //步骤
        //查询
        try {
            Scan scan = new Scan();
            FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ALL);
            filterList.addFilter(new SingleColumnValueFilter(Bytes.toBytes("info"),Bytes.toBytes("goodsId"), CompareFilter.CompareOp.EQUAL,page.getGoodsId().getBytes()));
            filterList.addFilter(new SingleColumnValueFilter(Bytes.toBytes("info"),Bytes.toBytes("source"), CompareFilter.CompareOp.EQUAL,page.getSource().getBytes()));
            scan.setFilter(filterList);
            ResultScanner tableScanner = table.getScanner(scan);
            Result result = tableScanner.next();
            if (result != null){
                String key = Bytes.toString(result.getRow());
                //更新操作
                updataToHBase(page,key);
                logger.info("更新：线程名：{}，url：{}，售价(注：列表页面为0.0)：{}", Thread.currentThread().getName(), page.getUrl(), page.getPrice());

            }else {
                //新增操作
                saveToHbase(page);
                logger.info("新增：线程名：{}，url：{}，售价(注：列表页面为0.0)：{}", Thread.currentThread().getName(), page.getUrl(), page.getPrice());

            }
        }catch (IOException e){
            e.printStackTrace();
        }


    }

    /**
     *Hbase中没有的rowKey添加进去
     * @param page
     */
    private void saveToHbase(Page page) {
        byte[] rowKey = Bytes.toBytes(page.getId());
        commonDealWith(page,rowKey);
    }

    /**
     * 更新到Hbase
     * @param page
     * @param key
     */
    private void updataToHBase(Page page, String key) {
        byte[] rowKey = Bytes.toBytes(key);
        commonDealWith(page,rowKey);

    }

    private void commonDealWith(Page page, byte[] rowKey) {
        try {
            Put put = new Put(rowKey);
            put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("goodsId"),Bytes.toBytes(page.getGoodsId()));
            put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("source"),Bytes.toBytes(page.getSource()));
            put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("url"),Bytes.toBytes(page.getUrl()));
            put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("title"),Bytes.toBytes(page.getTitle()));
            put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("imageUrl"),Bytes.toBytes(page.getImageUrl()));
            put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("price"),Double.toString(page.getPrice()).getBytes());
            put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("commentCnt"),Integer.toString(page.getCommentCnt()).getBytes());
            put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("goodRate"),Double.toString(page.getGoodRate()).getBytes());
            put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("params"),Bytes.toBytes(page.getParams()));
            table.put(put);
        }catch (Exception e){
            e.printStackTrace();

        }

    }
}
