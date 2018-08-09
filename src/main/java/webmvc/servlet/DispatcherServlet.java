package webmvc.servlet;

import webmvc.annotation.*;
import webmvc.bean.Arg;
import webmvc.bean.Handler;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.util.*;

/**
 * DispatcherServlet 拦截所有请求  /*
 * 负责：
 * <p>
 * 1. 加载容器初始化配置信息
 * 2. 读取配置文件信息， 获取要扫描的包名
 * 3. 初始化 IOC 容器
 * 4. DI
 * 5. 初始化映射 HandlerMapping （利用反射）
 * 6. 重写 service 方法 ， service 方法调用自定义 dispatcher()
 * 7. dispatcher（） 获取 请求 URI，  请求参数， 对参数进行类型转化
 * 8. dispatcher（） 找到 URI 对应的 Controller方法，传入 args ，调用方法
 *
 * @author willon
 * @version 1.0
 * 联系方式： willon295@163.com
 * @since 18-8-8
 */
@WebServlet(urlPatterns = "/*", initParams = {@WebInitParam(name = "applicationContext", value = "applicationContext.properties")})
public class DispatcherServlet extends HttpServlet {

    /**
     * 配置文件
     */
    private Properties properties = new Properties();

    /**
     * 要扫描的包名
     */
    private String scanPackageName = "";

    /**
     * 存储全类名
     */
    private ArrayList<String> classNames = new ArrayList<>();
    /**
     * ioc 容器
     */
    private HashMap<String, Object> ioc = new HashMap<>();

    /**
     * URL 映射
     */
    private Map<String, Handler> handlerMappings = new HashMap<>();


    @Override
    public void init(ServletConfig config) throws ServletException {
        // 1. 读取初始化配置信息
        doReadConfig(config);
        // 2. 扫包，扫描所有的类
        doScanPackage(scanPackageName);
        // 3. 实例化 ，IOC 容器初始化
        doInstance();
        // 4. 依赖注入
        doAutoWired();
        // 5. 初始化映射关系
        doInitHandlerMapping();

        // 打印 mapping
        System.out.println("生成mapping ..");
        Collection<Handler> values = handlerMappings.values();
        for (Handler value : values) {
            String url = value.getUrl();
            System.out.println(url);
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doDispatcher(req, resp);
    }


    /**
     * 真正处理请求的方法
     * <p>
     * 1. 通过 请求 uri 从 handlerMapping 获取 对应的 handler
     * 2. 从 handler中获取 Controller实例，处理的方法，方法需要的参数名称，参数的类型
     * 3. 通过 request 请求中的参数名称 从 handler中获取相应的 参数位置、类型，将请求的 String类型数据进行类型转换
     * 4. 调用 方法， 获取返回值
     * 5. 将返回值输出到  浏览器
     *
     * @param req  请求
     * @param resp 响应
     */
    private void doDispatcher(HttpServletRequest req, HttpServletResponse resp) {

        if (handlerMappings.isEmpty()) {
            try {
                resp.getWriter().write("404 Not found");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        resp.setContentType("text/html;charset=utf-8");
        //获取请求的 uri
        String requestURI = req.getRequestURI();
        Handler handler = handlerMappings.get(requestURI);
        if (handler == null) {
            try {
                resp.getWriter().write("404 Not found");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        Method method = handler.getMethod();
        if (method == null) {
            return;
        }


        //构建一个参数数组，用于存放方法需要的所有参数
        Object[] params = new Object[handler.getArgNameIndexTypeMap().size()];
        Map<String, Arg> argAndIndexMap = handler.getArgNameIndexTypeMap();
        // 获取request的参数
        Map<String, String[]> reqMap = req.getParameterMap();
        for (String argName : argAndIndexMap.keySet()) {

            // 获取 这个参数名称 的值
            String[] values = reqMap.get(argName);
            // 获取 请求的参数信息， 位置、数据类型
            Arg arg = argAndIndexMap.get(argName);
            //如果只有一个参数
            if (values.length == 1) {
                // 获取数据类型 ， 进行数据类型转换
                Class<?> type = arg.getType();
                // 如果是 Integer ,Double ,需要解析
                if (type == Integer.class) {
                    int val = Integer.parseInt(values[0].trim());
                    params[arg.getIndex()] = val;
                    continue;
                } else if (type == Double.class) {
                    Double val = Double.parseDouble(values[0].trim());
                    params[arg.getIndex()] = val;
                    continue;
                }

                // 否则进行强制类型转换
                params[arg.getIndex()] = type.cast(values[0]);
                continue;
            }
            params[arg.getIndex()] = values;
        }
        try {
            // 获取 请求的 参数
            //执行对应的方法，传入参数， 获取返回值
            Object invoke = method.invoke(handler.getController(), params);
            resp.getWriter().write(invoke.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 1. 通过 web.xml 初始化参数， 读取配置文件路径
     * 读取配置文件内容， 加载配置信息
     * 加载到需要扫描的包 的 名称
     *
     * @param config servlet配置
     */
    private void doReadConfig(ServletConfig config) {
        String applicationContext = config.getInitParameter("applicationContext");
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(applicationContext);
        try {
            properties.load(is);
            // 加载 applicationContext.properties 配置文件 , 读取文件， 获取要扫描的包名
            String scan = properties.getProperty("componentScan");
            scanPackageName = scan.replaceAll("\\.", "\\/");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 扫描所有包，子包内的类
     * 将其全类名， bean的默认名称放入 map
     *
     * @param path 包名
     */
    private void doScanPackage(String path) {
        //获取文件的 url
        URL url = this.getClass().getClassLoader().getResource(path);
        if (url != null) {
            //获取文件名称
            String fileName = url.getFile();
            if (!"".equals(fileName)) {
                //获取文件
                File baseFile = new File(fileName);
                //如果是文件夹 遍历里面所有文件，子文件夹
                if (baseFile.isDirectory()) {
                    File[] files = baseFile.listFiles();
                    if (files != null) {
                        //遍历文件
                        for (File f : files) {
                            //如果是文件夹 ， 递归扫描
                            if (f.isDirectory()) {
                                String url2 = path + "/" + f.getName();
                                doScanPackage(url2);
                            } else {
                                //文件 ， 将全类名  和 默认实例 名称 存入缓存map
                                putFullClassName2List(path, f);
                            }

                        }
                    }
                } else {
                    //是文件
                    putFullClassName2List(path, baseFile);
                }
            }
        }

    }

    /**
     * 将 文件 的全类名 放入 list
     *
     * @param parentPath 文件所在文件夹路径
     * @param baseFile   文件名
     */
    private void putFullClassName2List(String parentPath, File baseFile) {
        String upperBeanName = baseFile.getName().split("\\.")[0];
        String fullFileName = parentPath + "." + upperBeanName;
        String className = fullFileName.replaceAll("/", "\\.");
        classNames.add(className);

    }


    /**
     * 通过类 全名 ， 开始实例化
     */
    private void doInstance() {

        if (classNames.isEmpty()) {
            return;
        }
        for (String className : classNames) {
            String[] split = className.split("\\.");
            String beanName = firstNameLower(split[split.length - 1]);
            try {
                Class<?> clazz = Class.forName(className);
                if (clazz.isAnnotationPresent(Controller.class)) {
                    ioc.put(beanName, clazz.newInstance());
                } else if (clazz.isAnnotationPresent(Component.class)) {
                    Object instance = clazz.newInstance();
                    Component component = clazz.getAnnotation(Component.class);
                    String name = component.name();
                    if (!"".equals(name.trim())) {
                        ioc.put(name, instance);
                        continue;
                    }

                    Class<?>[] interfaces = clazz.getInterfaces();
                    if (interfaces.length > 0) {
                        for (Class<?> i : interfaces) {
                            ioc.put(firstNameLower(i.getSimpleName()), instance);
                        }
                        continue;
                    }

                    ioc.put(beanName, instance);

                } else if (clazz.isAnnotationPresent(Service.class)) {
                    // 第一种， 手动指定 名称
                    // 第二种 ，利用接口名称作为key ， 实现类为value
                    // 第三种， 默认首字母小写名字
                    Service service = clazz.getAnnotation(Service.class);
                    Object instance = clazz.newInstance();
                    String name = service.name();
                    // 手动指定
                    if (!"".equals(name.trim())) {
                        ioc.put(name, instance);
                        continue;
                    }
                    // 接口 首字母小写
                    Class<?>[] interfaces = clazz.getInterfaces();
                    if (interfaces.length > 0) {
                        for (Class<?> i : interfaces) {
                            ioc.put(firstNameLower(i.getSimpleName()), instance);
                        }
                        continue;
                    }

                    // 默认 首字母小写
                    ioc.put(beanName, instance);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 自动注入 ,DI
     */
    private void doAutoWired() {
        if (ioc.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Object> entry : ioc.entrySet()) {
            Field[] fields = entry.getValue().getClass().getDeclaredFields();
            // 如果字段 存在 @Resource 、@AutoWired 注解
            for (Field field : fields) {

                //如果是 Resource 注解， 判断其 name 属性
                if (field.isAnnotationPresent(Resource.class)) {
                    Resource resource = field.getAnnotation(Resource.class);
                    String beanName = resource.name();
                    // 如果为空 ， 字段类名首字母小写
                    if ("".equals(beanName.trim())) {
                        beanName = firstNameLower(field.getType().getSimpleName());
                    }
                    //授权访问 private
                    field.setAccessible(true);
                    try {
                        // 给拥有 此字段的 类  的 此字段  设置 新的值
                        // set(拥有此字段的类实例，  此字段新的值)
                        field.set(entry.getValue(), ioc.get(beanName));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }

                } else if (field.isAnnotationPresent(AutoWired.class)) {
                    //直接装配类名小写
                    String beanName = firstNameLower(field.getType().getSimpleName());
                    try {
                        //授权访问 private
                        field.setAccessible(true);
                        field.set(entry.getValue(), ioc.get(beanName));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }


    /**
     * URL 映射处理
     * 1. 遍历每个 Controller
     * 2. 读取 Controller 的  @RequestMapping 注解，获取根级 url
     * 3. 遍历每个方法，  @RequestMapping 注解，获取子级 url
     * 3.1  方法每个参数， 读取 @RequestParam 注解，记录 参数名，参数位置，参数类型
     * 4. 将 controller 实例， 方法，参数信息 放入 handler
     * 5. 将 <url,handler> 放入 handlerMapping
     */
    private void doInitHandlerMapping() {

        if (ioc.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Object> entry : ioc.entrySet()) {
            Class<?> clazz = entry.getValue().getClass();
            // 如果不是 Controller 类 ，不做处理
            if (!clazz.isAnnotationPresent(Controller.class)) {
                continue;
            }

            // 获取 Controller 的 URL
            RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);
            String url = requestMapping.value();

            // 遍历每个方法， 将其 获取 url，拼接后 加入映射列表
            Method[] methods = clazz.getDeclaredMethods();
            if (methods.length > 0) {
                for (Method method : methods) {
                    if (!method.isAnnotationPresent(RequestMapping.class)) {
                        continue;
                    }
                    RequestMapping req = method.getAnnotation(RequestMapping.class);

                    // 获取方法上的 url
                    String url2 = req.value();
                    String mappingUrl = (url + "/" + url2).replaceAll("/+", "/");
                    Handler handler = new Handler();
                    handler.setUrl(mappingUrl);
                    // 获取方法的参数
                    Parameter[] parameters = method.getParameters();
                    if (parameters.length > 0) {
                        for (int i = 0; i < parameters.length; i++) {
                            Parameter parameter = parameters[i];
                            //如果被  RequestParam 修饰， 将 自定义的 参数名记录
                            if (!parameter.isAnnotationPresent(RequestParam.class)) {
                                //获取到修饰的参数名
                                continue;
                            }


                            // 记录参数的 类型
                            RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
                            String name = requestParam.name();

                            //设置参数的信息
                            Arg arg = new Arg(i, parameter.getType());
                            // 将自定义的 name
                            handler.getArgNameIndexTypeMap().put(name, arg);
                        }
                    }
                    handler.setController(entry.getValue());
                    handler.setMethod(method);
                    handlerMappings.put(mappingUrl, handler);
                }
            }
        }
    }

    /**
     * 将字符串首字母小写
     *
     * @param name 字符串
     * @return 首字母小写的字符串
     */
    private String firstNameLower(String name) {
        char i = (char) (name.charAt(0) + 32);
        return name.replace(name.charAt(0), i);
    }

}
