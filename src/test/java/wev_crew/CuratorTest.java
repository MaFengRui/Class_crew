package wev_crew;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * Created with IDEA
 * author 光明顶斗士
 * Date:18-12-11
 * Time:上午10:36
 * Vision:1.1
 * Description:
 */
public class CuratorTest {
    public static void main(String[] args) {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.newClient("", retryPolicy);
        client.start();
        client.getCuratorListenable();
    }
}
