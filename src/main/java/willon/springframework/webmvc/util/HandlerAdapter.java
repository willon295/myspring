package willon.springframework.webmvc.util;

import lombok.extern.slf4j.Slf4j;
import willon.springframework.http.MediaType;
import willon.springframework.webmvc.annotation.RequestMethod;
import willon.springframework.webmvc.bean.ApplicationContext;
import willon.springframework.webmvc.handler.ErrorHandler;
import willon.springframework.webmvc.handler.Handler;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 请求路径匹配器
 * <p>
 * 通过 request 获取对应的 handler
 *
 * @author willon
 * @version 1.0
 */
@Slf4j
public class HandlerAdapter {


    private static List<Handler> handlers = ApplicationContext.getHandlers();
    private static ErrorHandler defaultHandler = new ErrorHandler();

    /**
     * 通过请求, 获取对应 Handler
     *
     * @param request 请求
     * @return 处理器
     */
    public static Handler match(HttpServletRequest request) {

        RequestMethod requestMethod = RequestMethod.valueOf(request.getMethod().trim());
        String requestURI = request.getRequestURI();
        log.info("requestURI: " + requestURI);
        String contentType = request.getContentType();
        if (contentType == null
                || (!contentType.equals(MediaType.APPLICATION_JSON.name())
                && !contentType.equals(MediaType.APPLICATION_JSON.value()))) {
            contentType = "*/*";
        }

        for (Handler handler : handlers) {
            // 1.  匹配当前请求 URI， 从处理器中获取匹配模式，如果没找到，继续匹配下一个handler
            Pattern pattern = handler.getPattern();
            boolean find = pattern.matcher(requestURI).find();
            if (!find) {
                continue;
            }

            // 2.  匹配 httpMethod 请求方法
            List<RequestMethod> httpMethods = handler.getHttpMethods();
            if (!httpMethods.contains(requestMethod)) {
                continue;
            }

            // 3. 匹配 Content-type, 若果没找到，继续匹配下一个handler

            MediaType handlerContentType = handler.getContentType();
            if (contentType.equals(handlerContentType.name()) || contentType.equals(handlerContentType.value())) {
                return handler;
            }


            // TODO 4. 根据具体的参数类型， 参数个数， bean 类型找到具体的 Handler
            return handler;

        }
        //如果没有找到， 使用默认的 处理器
        return defaultHandler;
    }


}
