package willon.springframework.webmvc.annotation;

import java.lang.annotation.*;

/**
 * Service 注解,作用与Component相同
 *
 * @author willon
 * @version 1.0
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Service {
    String name() default "";
}
