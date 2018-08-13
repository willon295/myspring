package willon.springframework.webmvc.annotation;

import java.lang.annotation.*;

/**
 * Created By willon
 *
 * @author willon
 * @version 1.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestBody {
}
