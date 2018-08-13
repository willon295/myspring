package willon.springframework.webmvc.annotation;

import java.lang.annotation.*;

/**
 * 请求 url 注解
 *
 * @author willon
 * @version 1.0
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestMapping {

    String value();

    RequestMethod[] method() default {};


}
