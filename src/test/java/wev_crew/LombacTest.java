package wev_crew;

import com.qianfeng.web_crew.domian.Page;
import org.junit.Test;

/**
 * Created with IDEA
 * author 光明顶斗士
 * Date:18-12-4
 * Time:下午9:13
 * Vision:1.1
 * Description:
 */
public class LombacTest {
    @Test
    public  void testPage(){
        Page page = new Page();
        page.setId("41515");
        System.out.println(page.getId());


    }
}
