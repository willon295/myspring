package willon.springframework.webmvc.bean;

import willon.springframework.webmvc.handler.Handler;

import java.util.*;

/**
 * Created By willon
 *
 * @author willon
 * @version 1.0
 */
public class ApplicationContext {

    private static ApplicationContext instance = new ApplicationContext();
    private Class runClazz;
    private static final Properties properties;
    private static final List<String> classNames;
    private static final Map<String, Object> ioc;
    private static final List<Handler> handlers;

    static {
        properties = new Properties();
        classNames = new ArrayList<>();
        handlers = new ArrayList<>();
        ioc = new HashMap<>();
    }

    private ApplicationContext() {
    }


    public Object getBean(String beanName) {
        return ioc.get(beanName);
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public Class getRunClazz() {
        return runClazz;
    }

    public void setRunClazz(Class runClazz) {
        this.runClazz = runClazz;
    }

    public Properties getProperties() {
        return properties;
    }


    public List<String> getClassNames() {
        return classNames;
    }

    public static Map<String, Object> getIoc() {
        return ioc;
    }


    public static List<Handler> getHandlers() {
        return handlers;
    }

    public static ApplicationContext getInstance() {
        return instance;
    }
}
