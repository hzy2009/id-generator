package com.hzy.id.generator;

import org.springframework.context.ApplicationContext;

public class SpringContextHolder {

    private static ApplicationContext applicationContext;
    
    public SpringContextHolder(ApplicationContext _applicationContext) {
    	applicationContext = _applicationContext;
	}

    public static ApplicationContext getApplicationContext() {
        assertApplicationContext();
        return applicationContext;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(String beanName) {
        assertApplicationContext();
        return (T) applicationContext.getBean(beanName);
    }

    public static <T> T getBean(Class<T> requiredType) {
        assertApplicationContext();
        return applicationContext.getBean(requiredType);
    }
    
    public static String getEvnProperties(String key) {
    	assertApplicationContext();
    	return applicationContext.getEnvironment().getProperty(key);
    }
    
    public static String getEvnProperties(String key, String defaultValue) {
    	assertApplicationContext();
    	return applicationContext.getEnvironment().getProperty(key, defaultValue);
    }

    private static void assertApplicationContext() {
        if (SpringContextHolder.applicationContext == null) {
            throw new RuntimeException("applicaitonContext属性为null,请检查是否注入了SpringContextHolder!");
        }
    }

}
