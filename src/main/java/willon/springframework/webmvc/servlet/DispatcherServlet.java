package willon.springframework.webmvc.servlet;

import lombok.extern.slf4j.Slf4j;
import willon.springframework.boot.databind.PathVariable;
import willon.springframework.http.MediaType;
import willon.springframework.webmvc.annotation.*;
import willon.springframework.webmvc.bean.ApplicationContext;
import willon.springframework.webmvc.bean.Arg;
import willon.springframework.webmvc.handler.Handler;
import willon.springframework.webmvc.util.HandlerAdapter;
import willon.springframework.webmvc.util.RequestExecutor;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * Created By willon
 *
 * @author willon
 * @version 1.0
 */
@Slf4j
public class DispatcherServlet extends HttpServlet {

    /**
     * 处理请求的 handlerMap
     */

    @Override
    public void init() {

        //  TODO 增加 MultiPartResolver

        // 1. 扫描所有 Controller ， 生成 映射-处理器 mappings
        initHandlerMapping();

        //  TODO 增加  ViewResolver

    }


    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        dispatcher(req, resp);
    }

    private void dispatcher(HttpServletRequest req, HttpServletResponse resp) {
        // 找到 Handler
        Handler handler = HandlerAdapter.match(req);
        log.info("Content-type: " + req.getContentType());
        log.info("Find handler ..");
        log.info(handler.toString());
        // 执行请求
        RequestExecutor.execute(req, resp, handler);
    }


    /**
     * 加载 @Controller  初始化 映射关系
     * <p>
     * 1. 获取类上 @RequestMapping 的 value (uri)
     * 2. 获取方法上  @RequestMapping 的 value (uri) ,method
     * 3. 获取方法上  参数信息， 记录参数的 位置， 对应的参数类型
     */

    private void initHandlerMapping() {

        if (ApplicationContext.getIoc().isEmpty()) {
            return;
        }
        for (Object currentInstance : ApplicationContext.getIoc().values()) {
            Class<?> clazz = currentInstance.getClass();
            if (!clazz.isAnnotationPresent(Controller.class) && !clazz.isAnnotationPresent(RestController.class)) {
                continue;
            }
            // 获取 根 uri
            RequestMapping clazzReqMap = clazz.getAnnotation(RequestMapping.class);
            String rootUri = clazzReqMap.value();
            //  获取 所有的方法
            Method[] methods = clazz.getDeclaredMethods();
            if (methods.length > 0) {

                for (Method method : methods) {
                    RequestMapping methodReqMap = method.getAnnotation(RequestMapping.class);
                    // 获取子 uri
                    String subUri = methodReqMap.value();
                    String uri = (rootUri + subUri).replaceAll("/+", "/");
                    String originPattern = uri.replaceAll("(\\{\\w+})", "\\\\w+");
                    String pattern = "^" + originPattern + "/?$";
                    pattern = pattern.replaceAll("/+", "/");
                    // 取出 请求方法，用于存入 映射 mapping 的 key
                    RequestMethod[] requestMethods = methodReqMap.method();

                    Handler handler = new Handler();
                    //获取方法参数列表
                    Parameter[] parameters = method.getParameters();
                    if (parameters.length > 0) {
                        for (int i = 0; i < parameters.length; i++) {
                            Parameter parameter = parameters[i];
                            // 三大注解判断  ： @RequestParam  , @RequestBody， @PathVariable ，
                            if (parameter.isAnnotationPresent(RequestParam.class)) {
                                String paramName = parameter.getAnnotation(RequestParam.class).name();
                                Arg arg = new Arg();
                                arg.setIndex(i);
                                arg.setType(parameter.getType());
                                handler.getArgMap().put(paramName, arg);
                            } else if (parameter.isAnnotationPresent(RequestBody.class)) {
                                String paramName = parameter.getName();
                                Arg arg = new Arg();
                                arg.setIndex(i);
                                arg.setType(parameter.getType());
                                handler.getArgMap().put(paramName, arg);
                                // 设置 content-type 为 application/json
                                handler.setContentType(MediaType.APPLICATION_JSON);
                            } else if (parameter.isAnnotationPresent(PathVariable.class)) {
                                // 如果是 restful 风格的占位符
                                PathVariable pathVariable = parameter.getAnnotation(PathVariable.class);
                                // 获取占位符名称
                                String pathName = pathVariable.name();
                                // 将 其设置为参数
                                Arg arg = new Arg();
                                arg.setIndex(i);
                                arg.setType(parameter.getType());
                                handler.getArgMap().put(pathName, arg);
                            }
                        }
                    }
                    handler.setMethod(method);
                    handler.setHttpMethods(Arrays.asList(requestMethods));
                    handler.setPattern(Pattern.compile(pattern));
                    handler.setController(currentInstance);
                    ApplicationContext.getHandlers().add(handler);
                }
            }
        }

    }

}
