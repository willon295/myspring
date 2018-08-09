package webmvc.annotation;

import java.lang.annotation.*;

/**
 * 请求参数注解
 *
 * @author willon
 * @version 1.0
 * 联系方式： willon295@163.com
 * @since 18-8-8
 */

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestParam {
    String name() default "";
}
