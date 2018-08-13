package willon.springframework.webmvc.annotation;

import java.lang.annotation.*;

/**
 * 自动装配 注解
 * 可手动指定注入的实例名
 *
 * @author willon
 * @version 1.0
 */

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Resource {
    String name() default "";
}
