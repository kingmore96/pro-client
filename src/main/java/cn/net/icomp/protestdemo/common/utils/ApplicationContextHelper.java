package cn.net.icomp.protestdemo.common.utils;

import org.springframework.context.ApplicationContext;

/**
 * @author: wg@icomp.net.cn
 * @date：2019/6/4 15:30
 * @version：1.0
 * @description: spring 上下文辅助类
 **/
public class ApplicationContextHelper {
    private static ApplicationContext applicationContext;

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static void setApplicationContext(ApplicationContext applicationContext) {
        ApplicationContextHelper.applicationContext = applicationContext;
    }

    public static <T> T getBean(Class<T> calzz) {
        return ApplicationContextHelper.applicationContext.getBean(calzz);
    }

}
