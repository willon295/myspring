package willon.springframework.webmvc.annotation;

import java.lang.annotation.*;

/**
 * 组件注解
 *
 * @author willon
 * @version 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Component {
    String name() default "";
}
