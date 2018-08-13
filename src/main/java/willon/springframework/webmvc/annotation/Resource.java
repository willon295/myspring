package willon.springframework.webmvc.annotation;

import java.lang.annotation.*;

/**
 * 自动装配 注解
 * 可手动指定注入的实例名
 *
 * @author willon
 * @version 1.0
 * 联系方式： willon295@163.com
 * @since 18-8-8
 */

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Resource {
    String name() default "";
}
