package com.qianfeng.web_crew.constants;

/**
 * Created with IDEA
 * author 光明顶斗士
 * Date:18-12-4
 * Time:下午1:59
 * Vision:1.1
 * Description:共通的常亮
 */
public interface CommonConstants {
    /*
    运行的模式
     */
    String CRAWLER_JOB_RUN_MODE="crawler.job.run.mode"; //增加代码的可维护行

    /**
     *
     * 共同的配置信息资源文件名
     */
    String COMMON_CONFIG_FILE_NAME="conf.properties";

    String DBCP_COMMON_FILE_NAME = "dbcp-config.properties";
    String CONNNECTION_FALURE_MSG = "connection.failure,msg";
    //用户评价页面
    String CREW_GETCOMMENTCNTGOODRATE_URL= "https://club.jd.com/comment/productCommentSummaries.action?referenceIds=";
    /**
     * 接口
     */
    String IDOWLOADBIZ = "IDowloadBiz";
    String IPARSEBIZ = "IParseBiz";
    String ISTOREBIZ = "IStoreBiz";
    String IURLPREPOSITORYBIZ = "IUrlPrepositoryBiz";
    //随机种子
    String CROWER_SEED_URL = "crawler.seed.url";

    /**
     * 单个商品页面
     */
    String CRAWLER_GOODS_URL_PREFIX = "crawler.goods.url.prefix";
    //商品列表的前缀
    String CRAWLER_GOODS_LIST_URL_PREFIX = "crawler.goods.list.url.prefix";
    String CRAWLER_GOODS_ALL_URL_PREFIX = "crawler.jd.goods.all.url.prefix";
    //连接redis的配置
    String CRAWLER_REDIS_MAX_IDLE = "crawler.redis.maxIdle";
    String CRAWLER_REDIS_MAX_TOTAL = "crawler.redis.maxTotal";
    String CRAWLER_REDIS_MAX_WAIT_MILLIS = "crawler.redis.maxWaitMillis";
    String CRAWLER_REDIS_HOST = "crawler.redis.host";
    String CRAWLER_REDIS_PORT ="crawler.redis.port" ;
    String CRAWLER_REDIS_TIMEOUT ="crawler.redis.timeout" ;

    /**
     * redis 存储不同优先级的url对应的key
     */
    String CRAWLER_URL_REDIS_REPOSITORY_HIGHER_KEY = "crawler.url.redis.repository.higher.key";
    String CRAWLER_URL_REDIS_REPOSITORY_LOWER_KEY = "crawler.url.redis.repository.lower.key";
    String CRAWLER_URL_REDIS_REPOSITORY_OTHER_KEY = "crawler.url.redis.repository.other.key";
    String CRAWLER_URL_REDIS_REPOSITORY_COMMON_KEY = "crawler.url.redis.repository.common.key" ;
    String CRAWLER_URL_CLEAR_FIRST_FLG = "crawler.url.clear.first.flg";
    String CRAWLER_URL_CLEAR_OTHER_FLG= "crawler.url.clear.other.flg";
    String CRAWLER_ADMIN_NEW_ADD_SEED_KEY = "crawler.admin.new.add.seed.key";
}
