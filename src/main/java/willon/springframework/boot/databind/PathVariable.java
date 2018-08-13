package willon.springframework.boot.databind;

import java.lang.annotation.*;

/**
 * PathVariable 注解
 *
 * @author willon
 * @version 1.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PathVariable {
    String name();
}
