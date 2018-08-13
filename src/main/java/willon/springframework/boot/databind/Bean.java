package willon.springframework.boot.databind;

import java.lang.annotation.*;

/**
 * Spring Bean 注解
 *
 * @author willon
 * @version 1.0
 */

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Bean {
    String name();
}
