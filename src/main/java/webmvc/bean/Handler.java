package webmvc.bean;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Handler 实体类，
 * 记录 请求 的 URL， 对应的 Controller， 方法， 方法需要的参数
 *
 * @author willon
 * @version 1.0
 * 联系方式： willon295@163.com
 * @since 18-8-8
 */
public class Handler {

    /**
     * 映射的  uri
     */
    private String url;

    /**
     * 存放处理此 uri 的Controller 实例
     */
    private Object controller;

    /**
     * 处理此 uri 映射的方法
     */
    private Method method;

    /**
     * 存放方法的  参数名称 ， 参数顺序 , 参数类型
     */
    private Map<String, Arg> argNameIndexTypeMap;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Handler() {
        this.argNameIndexTypeMap = new HashMap<>();
    }

    public Object getController() {
        return controller;
    }

    public void setController(Object controller) {
        this.controller = controller;

    }

    public Map<String, Arg> getArgNameIndexTypeMap() {
        return argNameIndexTypeMap;
    }


}
