package wev_crew;

import com.qianfeng.web_crew.utils.DBCPUtils;
import com.qianfeng.web_crew.utils.PropertiesManagerUtil;
import org.junit.Test;

/**
 * Created with IDEA
 * author 光明顶斗士
 * Date:18-12-4
 * Time:下午7:29
 * Vision:1.1
 * Description:工具类测试
 */
public class DBUtilTest {
    /**
     *
     */
    @Test
    public void testProperManagerUtil(){
        System.out.println(PropertiesManagerUtil.mode);
    }
    @Test
    public void testDBCPUtils(){
        System.out.println(DBCPUtils.getConnection());
    }
}
