package willon.springframework.webmvc.annotation;

import java.lang.annotation.*;

/**
 * 请求参数注解
 *
 * @author willon
 * @version 1.0
 */

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestParam {
    String name();
}
