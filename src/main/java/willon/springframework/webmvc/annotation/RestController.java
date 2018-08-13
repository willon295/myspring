package willon.springframework.webmvc.annotation;

import java.lang.annotation.*;

/**
 * RestController 注解，被注解的Controller 全局返回 JSON
 *
 * @author willon
 * @version 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RestController {
}
