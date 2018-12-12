package wev_crew;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * Created with IDEA
 * author 光明顶斗士
 * Date:18-12-12
 * Time:上午11:42
 * Vision:1.1
 * Description:
 */
public class Hbase_test {
    private Table table;
    private Connection connection;

    @Test
    public void init() throws IOException {
        connection = ConnectionFactory.createConnection();
        boolean b = connection.getAdmin().tableExists(TableName.valueOf("tb_product_info"));
        this.table = connection.getTable(TableName.valueOf("tb_product_info"));
        System.out.println(b);
        System.out.println("connection  = " + connection);
        System.out.println("table  = " + table);
    }

    @Test
    public void testConnection() {

    }

    @Test
    public void testGetSpecialRow() throws IOException {
        Scan scan = new Scan();

        FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ALL);//AND
        filterList.addFilter(new SingleColumnValueFilter(Bytes.toBytes("info"),Bytes.toBytes("name"), CompareFilter.CompareOp.EQUAL,"XiaoMi 8".getBytes()));
        filterList.addFilter(new SingleColumnValueFilter(Bytes.toBytes("info"),Bytes.toBytes("from"), CompareFilter.CompareOp.EQUAL,"China".getBytes()));

        scan.setFilter(filterList);

        ResultScanner scanner = table.getScanner(scan);
        for (Result r : scanner) {
            for (Cell cell : r.rawCells()) {
                System.out.println(
                        "Rowkey-->" + Bytes.toString(r.getRow()) + "  " +
                                "Familiy:Quilifier-->" + "" + Bytes.toString(CellUtil.cloneQualifier(cell)) + "  " +
                                "Value-->" + Bytes.toString(CellUtil.cloneValue(cell)));
            }

        }
    }


    @Test
    public void testScan() throws IOException {
        ResultScanner rs = table.getScanner(Bytes.toBytes("info"));
        System.out.println("行键\t\t商品编号\t\t商品名\t\t售价");
        System.out.println("----------------------------------------------------------");
        //循环打印输出
        for (Result result : rs) {
            byte[] row = result.getRow();
            //byte [] family, byte [] qualifier
            byte[] goodsId = result.getValue(Bytes.toBytes("info"), Bytes.toBytes("goodsId"));
            byte[] title = result.getValue(Bytes.toBytes("info"), Bytes.toBytes("title"));
            byte[] price = result.getValue(Bytes.toBytes("info"), Bytes.toBytes("price"));
            System.out.println(new String(row) + "\t\t" + new String(goodsId) + "\t\t" + new String(title) + "\t\t" + new String(price));
        }
    }


}
