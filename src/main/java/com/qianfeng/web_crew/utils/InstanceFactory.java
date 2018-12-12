package com.qianfeng.web_crew.utils;

/**
 * Created with IDEA
 * author 光明顶斗士
 * Date:18-12-10
 * Time:上午10:55
 * Vision:1.1
 * Description:创建实例工厂类
 */
public class InstanceFactory {
    /**
     * 动态根据获得的接口是实现类的权限定名，然后使用反射机制获取类的实例
     */
    public  static  <T> T getInstance(String key){
        String implFullName = PropertiesManagerUtil.getPropertyValue(key);

        try {
            return (T) Class.forName(implFullName).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("获得实现类发生了异常！异常信息是："+e.getMessage());
        }


    }
}
