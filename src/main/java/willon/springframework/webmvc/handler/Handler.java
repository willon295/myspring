package willon.springframework.webmvc.handler;

import lombok.Data;
import willon.springframework.http.MediaType;
import willon.springframework.webmvc.annotation.RequestMethod;
import willon.springframework.webmvc.bean.Arg;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Handler 处理器类
 *
 * @author willon
 * @version 1.0
 */
@Data
public class Handler {
    /**
     * 处理请求的 controller
     */
    private Object controller;

    /**
     * 用于匹配最佳的处理器
     */
    private Pattern pattern;
    /**
     * http请求类型
     */
    private List<RequestMethod> httpMethods;
    /**
     * 处理请求的方法
     */
    private Method method;

    /**
     * 请求参数map
     */
    private Map<String, Arg> argMap = new HashMap<>();

    /**
     * Content-type
     */
    private MediaType contentType = MediaType.ALL;

    @Override
    public String toString() {
        return "Handler{" +
                "controller=" + controller +
                ", pattern=" + pattern +
                ", httpMethods=" + Arrays.toString(httpMethods.toArray()) +
                ", method=" + method +
                ", argMap=" + argMap +
                ", contentType='" + contentType + '\'' +
                '}';
    }
}
