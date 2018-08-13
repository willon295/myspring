package willon.springframework.boot;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import willon.springframework.boot.databind.Bean;
import willon.springframework.boot.databind.Configuration;
import willon.springframework.webmvc.annotation.*;
import willon.springframework.webmvc.bean.ApplicationContext;
import willon.springframework.webmvc.servlet.DispatcherServlet;
import willon.springframework.webmvc.util.StringUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;

/**
 * 程序入口执行类
 *
 * @author willon
 */
@Slf4j
public class SpringApplication {

    private static ApplicationContext context;

    private static final Integer DEFAULT_TOMCAT_PORT = 8080;

    /**
     * 对运行的信息进行初始化
     *
     * @param clazz 运行的类
     */
    private static void init(Class clazz) {
        context = ApplicationContext.getInstance();
        context.setRunClazz(clazz);
    }

    public static void run(Class clazz, String[] args) {
        //初始化
        init(clazz);
        // 1. 加载配置文件
        loadConfig();
        // 2. 读取 clazz 所在的包名， 扫描包内 所有类
        scanPackage();
        // 3. 对所有组件 进行 实例化 ， 完成 IOC
        instantiated();
        // 4. 对所有的 组件 进行 DI
        dependencyInjection();
        // 5. 初始化并且启动 tomcat
        initAndStartTomcat();

    }

    /**
     * 加载配置文件，读取其中 服务器运行的端口号等信息
     */
    private static void loadConfig() {
        InputStream in = ClassLoader.getSystemResourceAsStream("application.properties");
        try {
            context.getProperties().load(in);
            String path = context.getRunClazz().getPackage().getName().replaceAll("\\.", "/");
            context.getProperties().setProperty("componentScan", path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 扫描所有的包
     */
    private static void scanPackage() {
        String path = context.getProperties().getProperty("componentScan");
        scanPackage(path);
    }

    /**
     * 扫描包
     *
     * @param path 包所在路径
     */
    private static void scanPackage(String path) {
        if ("".equals(path) || null == path) {
            return;
        }
        URL url = ClassLoader.getSystemResource(path);
        String packageName = url.getFile();
        File pack = new File(packageName);
        if (pack.isDirectory()) {
            File[] files = pack.listFiles();
            if (files == null) {
                return;
            }
            for (File file : files) {
                if (file.isDirectory()) {
                    // 如果是文件夹，递归扫包
                    String name = file.getName();
                    scanPackage(path + "/" + name);
                } else {
                    String name = file.getName();
                    String fullClassName = (path + "/" + name.split("\\.")[0]).replaceAll("/+", "\\.");
                    context.getClassNames().add(fullClassName);
                }
            }
        } else {
            String name = pack.getName();
            String fullClassName = (path + "/" + name.split("\\.")[0]).replaceAll("/+", "\\.");
            context.getClassNames().add(fullClassName);
        }

    }

    /**
     * 实例化 Service 、 Component 、Controller、Configuration 组件
     */
    private static void instantiated() {
        if (context.getClassNames().isEmpty()) {
            return;
        }
        // 判断是否为组件， 是 Service 、 Component 、Controller 、Configuration 则 实例化
        for (String className : context.getClassNames()) {
            try {
                String beanName = "";
                Class<?> clazz = Class.forName(className);
                if (clazz.isAnnotationPresent(Component.class) || clazz.isAnnotationPresent(Service.class)) {
                    // 分为三中情况 ， 优先级如下
                    //  1. 手动指定 name
                    //  2. 使用接口命名
                    //  3. 类名小写
                    Object instance = clazz.newInstance();
                    if (clazz.isAnnotationPresent(Component.class)) {
                        Component component = clazz.getAnnotation(Component.class);
                        beanName = component.name();
                    } else if (clazz.isAnnotationPresent(Service.class)) {
                        Service service = clazz.getAnnotation(Service.class);
                        beanName = service.name();
                    }

                    if (!"".equals(beanName)) {
                        ApplicationContext.getIoc().put(beanName, instance);
                        continue;
                    }
                    // 使用接口 全名首字母小写
                    Class<?>[] interfaces = clazz.getInterfaces();
                    if (interfaces.length > 0) {
                        for (Class<?> i : interfaces) {
                            String simpleName = i.getSimpleName();
                            beanName = StringUtil.firstNameLower(simpleName);
                        }
                        ApplicationContext.getIoc().put(beanName, instance);
                        continue;
                    }
                    // 使用 类名小写
                    beanName = StringUtil.firstNameLower(clazz.getSimpleName());
                    ApplicationContext.getIoc().put(beanName, instance);
                } else if (clazz.isAnnotationPresent(Controller.class) || clazz.isAnnotationPresent(RestController.class)) {
                    Object instance = clazz.newInstance();
                    beanName = StringUtil.firstNameLower(clazz.getSimpleName());
                    ApplicationContext.getIoc().put(beanName, instance);
                } else if (clazz.isAnnotationPresent(Configuration.class)) {
                    Object currentInstance = clazz.newInstance();
                    // 扫描当前类的所有方法 ，如果 有 @Bean 注解， 实例化 ， 放入 IOC
                    Method[] methods = clazz.getDeclaredMethods();
                    if (methods.length < 1) {
                        continue;
                    }
                    for (Method method : methods) {

                        if (!method.isAnnotationPresent(Bean.class)) {
                            continue;
                        }
                        // 反射执行方法，获得返回值 Bean
                        Object invoke = method.invoke(currentInstance, null);
                        Bean bean = method.getAnnotation(Bean.class);
                        if (!"".equals(bean.name())) {
                            beanName = bean.name();
                        } else {
                            String simpleName = method.getReturnType().getSimpleName();
                            beanName = StringUtil.firstNameLower(simpleName);
                        }
                        ApplicationContext.getIoc().put(beanName, invoke);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 依赖注入
     */
    private static void dependencyInjection() {

        if (ApplicationContext.getIoc().isEmpty()) {
            return;
        }
        for (Object currentInstance : ApplicationContext.getIoc().values()) {
            Class clazz = currentInstance.getClass();
            //获取所有字段信息
            Field[] fields = clazz.getDeclaredFields();
            if (fields.length > 0) {
                for (Field field : fields) {
                    String name;
                    // 开启权限
                    field.setAccessible(true);
                    if (field.isAnnotationPresent(AutoWired.class)) {
                        name = StringUtil.firstNameLower(field.getType().getSimpleName());
                        // 直接注入 类型名称首字母小写  的实例
                        try {
                            field.setAccessible(true);
                            field.set(currentInstance, ApplicationContext.getIoc().get(name));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    } else if (field.isAnnotationPresent(Resource.class)) {

                        // 优先使用自定义 名称
                        Resource resource = field.getAnnotation(Resource.class);
                        name = resource.name();
                        Object injectInstance = ApplicationContext.getIoc().get(name);
                        if (!"".equals(name) && injectInstance != null) {
                            try {
                                field.setAccessible(true);
                                field.set(currentInstance, injectInstance);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                            continue;
                        }

                        // 否则使用 类型名称小写
                        name = StringUtil.firstNameLower(field.getType().getSimpleName());
                        try {
                            field.setAccessible(true);
                            field.set(currentInstance, ApplicationContext.getIoc().get(name));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    /**
     * 初始化并启动 tomcat
     */
    private static void initAndStartTomcat() {
        int port;
        String portString = context.getProperties().getProperty("server.port");
        if ("".equals(portString) || null == portString) {
            port = DEFAULT_TOMCAT_PORT;
        } else {
            port = Integer.parseInt(portString.trim());
        }
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(port);
        // 关闭自动部署
        tomcat.getHost().setAutoDeploy(false);
        // 创建上下文对象
        StandardContext context = new StandardContext();
        context.setPath("");
        context.addLifecycleListener(new Tomcat.FixContextListener());
        tomcat.getHost().addChild(context);
        tomcat.addServlet("", DispatcherServlet.class.getSimpleName(), new DispatcherServlet());
        context.addServletMappingDecoded("/*", DispatcherServlet.class.getSimpleName());
        try {
            tomcat.start();
            tomcat.getServer().await();
        } catch (LifecycleException e) {
            log.error("Tomcat failed to start .. " + e.getMessage());
        }

    }
}
