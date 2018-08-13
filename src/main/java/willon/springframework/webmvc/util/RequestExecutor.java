package willon.springframework.webmvc.util;

import cn.willon.dto.RestResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import willon.springframework.http.MediaType;
import willon.springframework.webmvc.annotation.RequestMapping;
import willon.springframework.webmvc.annotation.ResponseBody;
import willon.springframework.webmvc.annotation.RestController;
import willon.springframework.webmvc.bean.Arg;
import willon.springframework.webmvc.handler.ErrorHandler;
import willon.springframework.webmvc.handler.Handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 请求执行器
 *
 * @author willon
 * @version 1.0
 */
@Slf4j
public class RequestExecutor {

    /**
     * 是否 返回JSON 格式 数据
     */
    private static boolean jsonResponse = false;
    private static Object responseData;
    private static Pattern restPattern = Pattern.compile("\\{(\\w+)}");

    public static void execute(HttpServletRequest request, HttpServletResponse response, Handler handler) {
        response.setContentType("text/html;charset=utf-8");
        if (handler instanceof ErrorHandler) {
            try {
                responseData = new RestResponse("404", "无法处理该请求");
                response.getWriter().write(JSON.toJSONString(responseData));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        // 1. 判断方法是否有 @ResponseBody 注解
        Method method = handler.getMethod();
        Object controller = handler.getController();
        MediaType contentType = handler.getContentType();
        Class<?> controllerClass = controller.getClass();
        // 如果方法 有  @ResponseBody 注解， 或者 Controller 有 @RestController注解
        if (method.isAnnotationPresent(ResponseBody.class) || controllerClass.isAnnotationPresent(RestController.class)) {
            jsonResponse = true;
        }


        // 1. 如果是 JSON 的请求
        if (contentType == MediaType.APPLICATION_JSON) {
            String line;
            StringBuilder sb = new StringBuilder();
            InputStreamReader in;
            try {
                in = new InputStreamReader(request.getInputStream());
                BufferedReader br = new BufferedReader(in);
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                JSONObject jsonObject = (JSONObject) JSON.parse(sb.toString());
                if (jsonObject != null) {
                    Arg arg0 = handler.getArgMap().get("arg0");
                    //获取参数的 类型
                    Class type = arg0.getType();
                    //强转成 java 对象
                    Object o = jsonObject.toJavaObject(type);
                    Object invoke = method.invoke(controller, o);
                    responseData = JSON.toJSON(invoke);
                    if (jsonResponse) {
                        response.getWriter().write(responseData.toString());
                        return;
                    }
                    // TODO 添加 ViewResolver
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        RequestMapping controllerClassAnnotation = controllerClass.getAnnotation(RequestMapping.class);
        String baseMapURI = controllerClassAnnotation.value();
        RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
        String mappingURI = requestMapping.value();
        mappingURI = baseMapURI + mappingURI;
        // 2. Restful 风格, 判断 URI 是否含有  {}  占位符
        Matcher matcher = restPattern.matcher(mappingURI);
        if (matcher.find()) {
            int argSize = handler.getArgMap().size();
            Object[] args = new Object[argSize];
            String requestURI = request.getRequestURI();

            Map<String, String> mapping = StringUtil.getMapping(requestURI, mappingURI);
            for (String argName : mapping.keySet()) {
                Arg arg = handler.getArgMap().get(argName);

                log.info(arg.toString());
                Class type = arg.getType();
                Integer index = arg.getIndex();
                // 将数据强制转换
                try {
                    Object o = StringUtil.castString2Target(mapping.get(argName), type);
                    args[index] = o;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // 反射执行 方法
            try {
                Object invoke = method.invoke(controller, args);
                if (jsonResponse) {
                    responseData = JSON.toJSON(invoke);
                    response.getWriter().write(responseData.toString());
                }
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 3. 普通带参数 的请求,

        // 获取 requestMap , handler的 argMap
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Arg> argMap = handler.getArgMap();
        //如果参数个数不符合， 则考虑将 数据封装成对象
        if (parameterMap.size() != argMap.size()) {

            HashMap<String, String> param = new HashMap<>(8);
            for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                String key = entry.getKey();
                String value = null;
                String[] values = entry.getValue();
                if (values.length <= 1) {
                    // TODO 封装成对象
                    value = values[0];
                }
                param.put(key, value);
            }

            Object parse = JSON.toJSON(param);
            Class aClass = argMap.get("arg0").getType();

            Object object = JSON.toJavaObject((JSON) parse, aClass);

            try {
                Object invoke = method.invoke(controller, object);
                if (jsonResponse) {
                    responseData = JSON.toJSON(invoke);
                    response.getWriter().write(responseData.toString());
                    return;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (argMap.size() > 0) {
            Object[] args = new Object[argMap.size()];
            for (Map.Entry<String, Arg> entry : argMap.entrySet()) {
                String argName = entry.getKey();
                log.info("argName: " + argName);
                Arg arg = entry.getValue();
                Integer index = arg.getIndex();
                Class type = arg.getType();
                String[] strings = parameterMap.get(argName);
                if (strings != null) {
                    if (strings.length > 1) {
                        args[index] = strings;
                    } else {
                        try {
                            Object o = StringUtil.castString2Target(strings[0], type);
                            args[index] = o;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
            try {
                Object invoke = method.invoke(controller, args);
                if (jsonResponse) {
                    responseData = JSON.toJSON(invoke);
                    response.getWriter().write(responseData.toString());
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // 直接指向请求
            try {
                Object invoke = method.invoke(controller, null);
                if (jsonResponse) {
                    responseData = JSONObject.toJSON(invoke);
                    response.getWriter().write(responseData.toString());
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


}
